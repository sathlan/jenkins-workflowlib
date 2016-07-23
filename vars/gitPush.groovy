// https://github.com/jenkinsci/workflow-cps-global-lib-plugin
def call(String remoteUrl, String branch, String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      sh("grep -q github.com ~/.ssh/known_hosts || ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts")
      sh("git remote -v | grep -q ${repoName} || git remote add ${repoName} ${remoteUrl}")
      sh("git -c core.askpass=true push ${repoName}")
    }
}
