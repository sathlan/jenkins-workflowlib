// https://github.com/jenkinsci/workflow-cps-global-lib-plugin
def call() {
  if (env.GIT_COMMIT == null) {
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    // short SHA, possibly better for chat notifications, etc.
    def gitShortCommit = gitCommit.take(6)
    env.GIT_COMMIT = gitCommit
    env.GIT_SHORT_COMMIT = gitShortCommit
  }
  env.GIT_COMMIT
}
