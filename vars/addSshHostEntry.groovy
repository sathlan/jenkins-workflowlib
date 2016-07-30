def call(String host) {
  sh("[ -d ~/.ssh ] || install -d -m 0700 ~/.ssh")
  sh("grep -q ${host} ~/.ssh/known_hosts || ssh-keyscan -t rsa ${host} >> ~/.ssh/known_hosts")
}
