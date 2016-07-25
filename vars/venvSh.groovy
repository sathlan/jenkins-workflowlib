def call(String cmd, String venv = 'venv') {
  sh("source ${venv}/bin/activate && ${cmd}")
}
