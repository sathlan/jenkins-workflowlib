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
