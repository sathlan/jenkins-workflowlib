def call(env, String dockerOpt='', body) {
  def workDir = pwd()
  env.withRun("-v ${workDir}:${workDir}:z -v ${workDir}@tmp:${workDir}@tmp:z ${dockerOpt}") { c ->
    body(c)
  }
}
