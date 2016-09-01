def call(String msg) {
  if (!env.DONT_ASK) {
    input message: msg, parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'YES' ]]
  }
}
