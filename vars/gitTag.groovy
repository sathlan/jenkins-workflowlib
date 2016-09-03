def call(String version) {
  sh("git tag ${version}")
}
