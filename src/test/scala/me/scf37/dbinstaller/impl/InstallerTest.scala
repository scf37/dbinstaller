package me.scf37.dbinstaller.impl

import java.io.File

import me.scf37.dbinstaller.api.Driver
import me.scf37.dbinstaller.api.InstallerException
import me.scf37.dbinstaller.api.ScriptEntry
import me.scf37.dbinstaller.config.DbInstallerConfig
import org.scalatest.FunSuite

import scala.collection.mutable

class InstallerTest extends FunSuite {
  test("positive flow") {
    val d = new TestDriver
    val i = new Installer(new DbInstallerConfig(Map("lstFile" -> "src/test/resources/1.lst")), d)

    i.run()

    assert(d.execList.map(_.getName).toList == List("a.script", "b.script", "c.script", "d.script"))

    d.execList.clear()
    i.run()

    assert(d.execList.map(_.getName).toList == List("c.script"))
  }

  test("throw if no lst file") {
    val d = new TestDriver
    val i = new Installer(new DbInstallerConfig(Map("lstFile" -> "nosuchfile")), d)

    try {
      i.run()
      assert(false, "should throw")
    } catch {
      case e: InstallerException if e.getMessage.contains("nosuchfile") =>
    }
  }

  test("throw if no script in file") {
    val d = new TestDriver
    val i = new Installer(new DbInstallerConfig(Map("lstFile" -> "src/test/resources/bad.lst")), d)

    try {
      i.run()
      assert(false, "should throw")
    } catch {
      case e: InstallerException if e.getMessage.contains("nosuchscript") =>
    }
  }

  class TestDriver extends Driver {
    val entries = mutable.ArrayBuffer.empty[ScriptEntry]
    val execList = mutable.ArrayBuffer.empty[File]

    override def name: String = "test"

    override def addEntry(entry: ScriptEntry): Unit = entries += entry

    override def usageString: String = "usage string"

    override def getLatestEntry(scriptName: String): Option[ScriptEntry] =
      entries.filter(_.name == scriptName).sortBy(- _.version).headOption

    override def close(): Unit = {}

    override def exec(script: File): Unit = execList += script

    override def open(config: DbInstallerConfig): Unit = {}
  }

}
