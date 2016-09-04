def call(String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      addSshHostEntry('github.com')
      (repo, branch) =  env.JOB_NAME.split("/")
      sh("git checkout ${branch}")
      sh("git git remote add github ${env.GITHUB_ACCOUNT}/${repo}.git")
      sh("git push github ${branch}")
    }
}
