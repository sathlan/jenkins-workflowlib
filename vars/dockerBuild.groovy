def call(String dockerName, Boolean isSystemd = true, Boolean needPuppet = false) {
  def dockerOpt = ''
  if (isSystemd) {
    dockerOpt = '-v /sys/fs/cgroup:/sys/fs/cgroup:ro -t'
  }
  def dockerBuildOpt = "."
  def imageName = "sathlan/${dockerName}"
  def rubyEnv = ["PATH=${pwd()}/bin:${env.PATH}", "GEM_HOME=${pwd()}/.bundled_gems"]
  def buildImage = "${imageName}:${env.BUILD_ID}"
  def myEnv

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
