package me.scf37.dbinstaller.api

import java.time.Instant

/**
  * Entry for every script execution.
  * script name + script version forms unique constraint here.
  */
case class ScriptEntry(
  /** script name */
  name: String,
  /** script version */
  version: Long,
  /** date this script have been applied */
  date: Instant,
  /** script contents. For manual use only so could be saved partially or not saved at all */
  contents: String,
  /** hash of script contents */
  hash: String
)
