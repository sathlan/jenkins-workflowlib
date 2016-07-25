def call(String cmd, String venv = 'venv') {
  sh(". ${venv}/bin/activate && ${cmd}")
}
