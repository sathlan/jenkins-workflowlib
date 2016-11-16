def call(String msg) {
  def answer = false
  if (env.DONT_ASK != 'true' && env.DONT_ASK != true) {
    node ('local') {
      matterMost 'good', "${env.BRANCH_NAME}: Asking about ${msg} (${env.BUILD_URL}/console)"
    }
    answer = input message: msg, parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'YES' ]]
  } else {
    answer = true
  }
  return answer
}
