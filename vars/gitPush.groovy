def call(String remoteUrl, String branch, String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      sh("ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts")
      sh("git remote -v | grep -q ${repoName} || git remote add ${repoName} ${remoteUrl}")
      sh("git -c core.askpass=true push ${repoName}")
    }
}
