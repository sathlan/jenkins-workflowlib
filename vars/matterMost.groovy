def call(String color, String msg) {
  if (env.MATTERMOST_INSTALLED) {
    mattermostSend color: color, message: msg
  }
}
