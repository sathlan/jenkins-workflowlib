def call(String msg) {
  if (!env.DONT_ASK) {
    node ('local') {
      matterMost 'good', "${env.BRANCH_NAME}: Asking about ${msg} (${env.BUILD_URL}/console)"
    }
    input message: msg, parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'YES' ]]
  }
}
