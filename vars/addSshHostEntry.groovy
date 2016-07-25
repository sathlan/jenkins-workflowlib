def call(String host) {
  sh("grep -q ${host} ~/.ssh/known_hosts || ssh-keyscan -t rsa ${host} >> ~/.ssh/known_hosts")
}
