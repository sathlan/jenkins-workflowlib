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
