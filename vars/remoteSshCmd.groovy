def call(String user, String host, String cmd, String port = '22') {
  sh "ssh -p ${port} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${user}@${host} '${cmd}'"
}
