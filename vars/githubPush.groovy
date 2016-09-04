def call(String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      addSshHostEntry('github.com')
      repo =  env.JOB_NAME.split("/")[0]
      branch =  env.JOB_NAME.split("/")[1]
      sh("git branch -d ${branch}")
      sh("git checkout -b ${branch}")
      sh("git remote | grep github || git remote add github ${env.GITHUB_ACCOUNT}/${repo}.git")
      sh("git push github ${branch}")
    }
}
