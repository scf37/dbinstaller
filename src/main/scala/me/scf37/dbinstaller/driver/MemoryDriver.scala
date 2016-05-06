package me.scf37.dbinstaller.driver

import java.io.File

import me.scf37.dbinstaller.api.Driver
import me.scf37.dbinstaller.api.ScriptEntry
import me.scf37.dbinstaller.config.DbInstallerConfig

import scala.collection.mutable

class MemoryDriver extends Driver {
  val entries = mutable.ArrayBuffer.empty[ScriptEntry]

  /**
    * @return driver name AKA driver url prefix
    */
  override def name: String = "memory"

  /**
    * Add new entry
    */
  override def addEntry(entry: ScriptEntry): Unit = entries += entry

  /**
    * @return usage help
    */
  override def usageString: String = "memory: Driver that does nothing. Useful for testing lst files."

  /**
    * Get latest (i.e. with maximum version) entry of this script
    * @return script entry if any or None
    */
  override def getLatestEntry(scriptName: String): Option[ScriptEntry] =
    entries.filter(_.name == scriptName).sortBy(- _.version).headOption

  /**
    * Close this driver and release used resources
    */
  override def close(): Unit = {}

  /**
    * Execute script by given file name against database
    */
  override def exec(script: File): Unit = {}

  /**
    * initialize this driver instance with provided config
    * @param config
    */
  override def open(config: DbInstallerConfig): Unit = {}
}
