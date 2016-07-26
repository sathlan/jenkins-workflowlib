def call(String msg) {
  input message: msg, parameters: [[$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'YES' ]]
}
