def call(String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      addSshHostEntry('github.com')
      sh("git -c core.askpass=true push ${repoName}")
    }
}
