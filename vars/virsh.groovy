def call(String host, String cmd, String user = 'root') {
  sh("virsh -c 'qemu+ssh://${user}@#{host}/system?no_verify=1&no_tty=1&known_hosts=/dev/null' ${cmd}")
}
