def call(String color, String msg) {
  if (env.MATTERMOST_SERVER) {
    mattermostSend color: color, message: msg
  }
}
