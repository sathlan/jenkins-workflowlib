// https://github.com/jenkinsci/workflow-cps-global-lib-plugin
def call(String remoteUrl, String credentialsId, String repoName = 'origin') {
    sshagent([credentialsId]) {
      addSshHostEntry(remoteUrl)
      sh("git -c core.askpass=true push ${repoName}")
    }
}
