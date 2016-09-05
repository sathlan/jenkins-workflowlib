def call(String version) {
  sh("git config --global release.remote github")
  withCredentials([[$class: 'StringBinding', credentialsId: 'github-api-token', variable: 'GITHUB_API_TOKEN']]) {
    sh("git config --global release.api-token ${env.GITHUB_API_TOKEN}")
    sh("git-release")
  }
}
