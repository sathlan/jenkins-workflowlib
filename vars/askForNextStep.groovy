def call(String msg, String description='', String color='good') {
  def answer = false
  if (env.DONT_ASK != 'true' && env.DONT_ASK != true) {
    node ('local') {
      matterMost(color, "${env.BRANCH_NAME}: Asking about ${msg} (${env.BUILD_URL}/console)")
    }
    answer = input message: msg, parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: description, name: 'YES' ]]
  } else {
    answer = true
  }
  return answer
}
