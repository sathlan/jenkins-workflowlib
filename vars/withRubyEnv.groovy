def call(Boolean init=false, body) {
  def rubyEnv = ["PATH=${pwd()}/bin:${env.PATH}", "GEM_HOME=${pwd()}/.bundled_gems"]
  withEnv(rubyEnv) {
    if(init) {
      sh "[ ! -e Gemfile.lock ] || rm Gemfile.lock"
      sh "bundle install --binstubs"
    }
    body()
  }
}
