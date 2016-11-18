def call(String desc, body) {
  stage("${desc}") {
    y = askForNextStep "${desc} ?"

    if (y) {
      node ('local') {
        sshagent([env.SSH_CREDENTIAL_ID]) {
          ansiColor() {
            body()
          }
        }
      }
    }
  }
}
