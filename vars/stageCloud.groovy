def call(String desc, String ssh_cred_id='jenkins', body) {
  stage("${desc}") {
    y = askForNextStep "${desc} ?"

    if (y) {
      node ('local') {
        sshagent([ssh_cred_id]) {
          ansiColor() {
            body()
          }
        }
      }
    }
  }
}
