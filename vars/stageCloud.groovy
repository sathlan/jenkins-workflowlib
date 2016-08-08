def call(String desc, body) {
  stage "${desc}"
  node ('local') {
    matterMost 'good', "${env.BRANCH_NAME}: ${desc} (${env.BUILD_URL}/console)"
  }
  y = askForNextStep "${desc} ?"

  if (y) {
    node ('local') {
      sshagent(['jenkins']) {
        ansiColor() {
          body()
        }
      }
    }
  }
}
