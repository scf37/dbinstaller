package me.scf37.dbinstaller.impl

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

import me.scf37.dbinstaller.api.Driver
import me.scf37.dbinstaller.api.InstallerException
import me.scf37.dbinstaller.api.ScriptEntry
import me.scf37.dbinstaller.config.DbInstallerConfig

class Installer(conf: DbInstallerConfig, driver: Driver) {

  def run(): Unit = {
    driver.open(conf)
    try {
      runLstFile(conf.workDir.resolve(conf.lstFile))
    } finally {
      driver.close()
    }
  }

  private[this] def runLstFile(lstFile: Path): Unit = {
    val base = lstFile.getParent

    lines(lstFile)
      .map(_.trim).filterNot(_.isEmpty).foreach {
      case line if line.startsWith("#") => //comment
      case line if line.startsWith("include ") => runLstFile(base.resolve(line.substring("include ".length)))
      case line if line.startsWith("once ") => runOnce(base, line.substring("once ".length))
      case line if line.startsWith("always ") => runAlways(base, line.substring("always ".length))
      case line  => run(base, line)

    }
  }

  /**
    * guarantee that will script will be executed once
    * @param base base path
    * @param script
    */
  private[this] def runOnce(base: Path, script: String): Unit = {
    doRun(base, script) { (f, lastEntry, nextEntry) =>
      if (lastEntry.exists(_.hash != nextEntry.hash)) {
        throw new InstallerException(s"Script '$script' have been changed but it is marked with runOnce (not designed to be re-entrant). Aborting.")
      }
      if (lastEntry.isEmpty) {
        driver.exec(f.toFile)
        driver.addEntry(nextEntry)
        println("+ " + script)
      } else {
        println("- " + script)
      }
    }
  }

  /**
    * Always execute this script
    * @param script
    */
  private[this] def runAlways(base: Path, script: String): Unit = {
    doRun(base, script) { (f, lastEntry, nextEntry) =>

      driver.exec(f.toFile)
      driver.addEntry(nextEntry)

      if (lastEntry.isDefined) {
        println("* " + script)
      } else {
        println("+ " + script)
      }
    }
  }

  /**
    * Execute script if it changed
    * @param script
    */
  private[this] def run(base: Path, script: String): Unit = {
    doRun(base, script) { (f, lastEntry, nextEntry) =>

      if (lastEntry.map(_.hash != nextEntry.hash).getOrElse(true)) {
        driver.exec(f.toFile)
        driver.addEntry(nextEntry)

        if (lastEntry.isDefined) {
          println("* " + script)
        } else {
          println("+ " + script)
        }

      } else {
        println("- " + script)
      }
    }
  }

  private[this] def doRun(base: Path, script:String)(save: (Path, Option[ScriptEntry], ScriptEntry) => Unit): Unit = {
    val f = base.resolve(script)
    val e = driver.getLatestEntry(script)
    val s = text(f)

    val nextEntry = ScriptEntry(
      name = script,
      version = e.map(_.version + 1).getOrElse(0),
      date = Instant.now(),
      contents = s,
      hash = hash(s)
    )

    save(f, e, nextEntry)
  }

  private[this] def hash(s: String) = {
    val md = MessageDigest.getInstance("SHA-1")
    Base64.getEncoder.withoutPadding().encodeToString(md.digest(s.getBytes("UTF-8")))
  }

  private[this] def text(p: Path): String = try {
    new String(Files.readAllBytes(p), "UTF-8")
  } catch {
    case e: Exception =>
      throw new InstallerException(s"Unable to read script file ${p.toString}: " + e.toString)
  }

  private[this] def lines(p: Path): Seq[String] = try {
    import scala.collection.JavaConverters._
    Files.readAllLines(p).asScala
  } catch {
    case e: IOException =>
      throw new InstallerException(s"Unable to read lst file ${p.toString}: " + e.toString)
  }

}



