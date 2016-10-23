def call(String dockerName, Boolean isSystemd = true, Boolean isApp = false, Boolean needPuppet = false, Boolean isPublic = false) {
  gitEnv()

  if (isApp) {
    dockerName = "app-${dockerName}"
  }

  def dockerOpt = ''
  def remotePuppetImage = ''

  def currentDir = "${pwd()}"

  if (isSystemd) {
    dockerOpt = '-v /sys/fs/cgroup:/sys/fs/cgroup:ro -t'
  }

  if (needPuppet) {
    remotePuppetImage = "${env.DOCKER_PRV_REGISTRY_HOSTNAME}/sathlan/puppet-client:latest"
  }


  def dockerBuildOpt = "."
  def imageName = "sathlan/${dockerName}"
  def rubyEnv = ["PATH=${pwd()}/bin:${env.PATH}", "GEM_HOME=${pwd()}/.bundled_gems"]
  def buildImage = "${imageName}:${env.BUILD_ID}"
  def myEnv

  timestamps {
    stage('Checkout') {
      checkout scm
    }

    ansiColor() {
      stage('Prepare env') {
        withEnv(rubyEnv) {
          sh "[ ! -e Gemfile.lock ] || rm Gemfile.lock"
          sh "bundle install --binstubs"
        }
      }

      docker.withRegistry("${env.DOCKER_PRV_REGISTRY}", 'docker-private-registry') {
        stage('build image') {
          myEnv = docker.build "${imageName}:${env.BUILD_ID}", "${dockerBuildOpt}"
        }

        if (needPuppet) {
          stage('puppet configure') {
            // detect if puppet files have changed
            def changed_files = sh(returnStdout: true, script: "git diff-tree --no-commit-id --name-only -r ${env.GIT_COMMIT}")
            // Empty list is the initial commit.
            if (changed_files =~ /(?m)^puppet/ || changed_files.allWhitespace) {
              docker.image(remotePuppetImage).withRun("${dockerOpt} -v /opt/puppetlabs") {p ->
                myEnv.withRun("${dockerOpt} --volumes-from ${p.id} -e PATH=/opt/puppetlabs/bin:${env.PATH} -v ${currentDir}:${currentDir}:z --entrypoint /usr/sbin/init -u 0") {m ->
                  sh "docker exec -t ${m.id} puppet module install --modulepath ${currentDir}/puppet/modules --target-dir ${currentDir}/puppet/modules puppetlabs-postgresql"
                  sh "docker exec -t ${m.id} puppet apply --debug --modulepath ${currentDir}/puppet/modules ${currentDir}/puppet/manifest.pp"
                  sh "docker commit -m 'Puppet configuration' ${m.id} ${buildImage}"
                }
              }
            }
          }
        }

        stage('publish image') {
          myEnv.withRun(dockerOpt) {c ->
            sh "rm -rf artifacts && mkdir -p artifacts"
            sh "docker inspect ${c.id} > artifacts/inspect.txt"
            sh "docker history ${buildImage} > artifacts/history.txt"
          }
        }

        stage('test image') {
          myEnv.withRun("${dockerOpt}") {c ->
            withEnv(rubyEnv + ["${dockerName.replaceAll('-','_').toUpperCase()}_ID=${c.id}", "LOCALHOST_ID=${c.id}", "DOCKER_IMAGE=${buildImage}"]) {
              sh "rake spec:${dockerName}"
              sh "rake spec:localhost"
            }
          }

          if (!isApp.isAllWhitespace()) {
            def dockerNameUp = appName.toUpperCase()
            def appVersion = "${dockerNameUp}_APP_VERSION"
            def appDir = "${dockerNameUp}_APP_DIR"
            withEnv(["${appVersion}=${env.BUILD_ID}", "${appDir}=${currentDir}/spec/fixture"]) {
              sh "env"
              sh "bash -x ./bin/app-${dockerName} --version"
            }
          }
        }

        stage('promote') {
          myEnv.push('latest')
          def tag = sh returnStdout: true, script: 'git describe --exact-match HEAD || true'
          tag = tag.trim()
          if (tag =~ /^v.*/) {
            myEnv.push(tag)
            sh "echo ${tag} > artifacts/RELEASE"
          }
          archive 'artifacts/'
          if(isPublic) {
            timeout(time: 1, unit: 'DAYS') {
              if (askForNextStep('Deploy to GitHub ?')) {
                lock("githubPush-docker-${dockerName}") {
                  githubPush('jenkins', 'github')
                  if (askForNextStep('Release to GitHub ?')) {
                    withEnv(rubyEnv) {
                      githubRelease()
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
