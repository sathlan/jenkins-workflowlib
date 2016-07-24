// https://github.com/jenkinsci/workflow-cps-global-lib-plugin
def call() {
   sh('git rev-parse HEAD > GIT_COMMIT')
   gitCommit=readFile('GIT_COMMIT')
   // short SHA, possibly better for chat notifications, etc.
   gitShortCommit=gitCommit.take(6)
   env.GIT_COMMIT=gitCommit
   env.GIT_SHORT_COMMIT=gitShortCommit
}
