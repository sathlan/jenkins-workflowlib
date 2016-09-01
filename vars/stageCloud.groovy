def call(String desc, body) {
  stage "${desc}"
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
