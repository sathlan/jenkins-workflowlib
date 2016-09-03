def newStep(String stage, String previous_stage, Boolean backup=false) {
  stageCloud("${stage}") {
    echo "STAGE ${stage}, with backup : ${backup}"
    applyPatch("${stage}")
    scpUndercloud("stages/${stage}.sh")
    sshUndercloud("./${stage}.sh")
    sshUndercloud("./log-system-info.sh post-${stage} post-${previous_stage} instack")
    if (backup) {
      backupMaybe("${stage}")
    }
  }
}
