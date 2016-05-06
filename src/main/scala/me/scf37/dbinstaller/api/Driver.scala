package me.scf37.dbinstaller.api

import java.io.File

import me.scf37.dbinstaller.config.DbInstallerConfig

/**
  * Driver to support particular database
  */
trait Driver {
  /**
    * @return driver name AKA driver url prefix
    */
  def name: String

  /**
    * @return usage help
    */
  def usageString: String

  /**
    * initialize this driver instance with provided config
    * @param config
    */
  def open(config: DbInstallerConfig): Unit

  /**
    * Close this driver and release used resources
    */
  def close(): Unit

  /**
    * Execute script by given file name against database
    */
  def exec(script: File): Unit

  /**
    * Get latest (i.e. with maximum version) entry of this script
    * @return script entry if any or None
    */
  def getLatestEntry(scriptName: String): Option[ScriptEntry]

  /**
    * Add new entry
    */
  def addEntry(entry: ScriptEntry): Unit
}
