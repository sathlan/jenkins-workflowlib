def call(String remoteUrl, String branch, String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      sh("git remote add ${repoName} ${remoteUrl}")
      sh("git -c core.askpass=true push ${repoName}")
    }
}
