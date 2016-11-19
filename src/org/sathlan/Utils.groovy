package org.sathlan;

def nonNullandNonEmpty(entry) {
  entry != null && !entry.isAllWhitespace()
}

def isTrue(value) {
  nonNullandNonEmpty(value) && (value == 'true' || value == true)
}

def isFalse(value) {
  nonNullandNonEmpty(value) && (value == 'false' || value == false)
}

def isTrueOrFalse(value) {
  isTrue(value) || isFalse(value)
}

// Find value from the env.NAME variable but with default fallback on
// 1. env.PREFIX_NAME_<BRANCH>
// 2. env.PREFIX_NAME

// The user can then set the value by project branch and global default.

// When using text property, you can set a DEFSTRING that will mean
// that the user didn't set the value. An example default string could
// be "CHANGEME".

// Throw an exception if no value is found and set the
// ERROR_MISSING_VARIABLE in the env to 'true'
def findDefaultValueFrom(env, String name, String prefix='', String defString = 'CHANGEME') {
  def value = ''
  def valuePrefix = "${prefix}_${name}"
  def valuePrefixBranch = "${prefix}_${name}_${env.BRANCH_NAME}"
  def msg = ''
  if (nonNullandNonEmpty(env.BRANCH_NAME)) {
    if (nonNullandNonEmpty(prefix)) {
      msg = "in ${valuePrefixBranch} and ${valuePrefix} in this order"
    } else {
      msg = "in ${valuePrefixBranch}"
    }
  } else {
    if (nonNullandNonEmpty(prefix)) {
      msg = "in ${valuePrefix}"
    } else {
      msg = ""
    }
  }
  if (nonNullandNonEmpty(env[name]) && env[name] != defString) {
    value = env[name]
  } else if (nonNullandNonEmpty(env.BRANCH_NAME) && nonNullandNonEmpty(env[valuePrefixBranch])) {
    value = env[valuePrefixBranch]
  } else if (nonNullandNonEmpty(prefix) && nonNullandNonEmpty(env[valuePrefix])) {
    value = env[valuePrefix]
  } else {
    env.ERROR_MISSING_VARIABLE = 'true'
    throw new Exception("Cannot find a proper value for ${name}.  Also searched ${msg}.  Please set one of them in your env.")
  }
  value
}

def findTrueFalseFrom(env, String name, String prefix = '', String fallback = 'false') {
  def trueFalse = ''
  if (isTrueOrFalse(env[name])) {
    trueFalse = env[name]
  } else if (utils.isTrueOrFalse(env["${prefix}_${name}_${env.BRANCH_NAME}"])) {
    trueFalse = env["${prefix}_${name}_${env.BRANCH_NAME}"]
  } else if (utils.isTrueOrFalse(env["${name}_${prefix}"])) {
    trueFalse = env["${name}_${prefix}"]
  } else {
    trueFalse = fallback
  }
  trueFalse
}
