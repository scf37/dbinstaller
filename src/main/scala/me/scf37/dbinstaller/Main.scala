package me.scf37.dbinstaller

import me.scf37.dbinstaller.api.Driver
import me.scf37.dbinstaller.api.InstallerException
import me.scf37.dbinstaller.config.DbInstallerConfig
import me.scf37.dbinstaller.driver.MemoryDriver
import me.scf37.dbinstaller.driver.MongoDriver
import me.scf37.dbinstaller.impl.Installer

import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
  * Created by asm on 04.05.16.
  */
object Main {

  val drivers = Seq(new MemoryDriver, new MongoDriver)

  def main(argv: Array[String]): Unit = {
    val conf = parseArgv(argv) match {
      case Success(source) => new DbInstallerConfig(source)
      case Failure(e) =>
        println(e.getMessage)
        printUsage(new DbInstallerConfig(Map.empty))
        System.exit(1)
        ???
    }

    if (conf.flags.errors.nonEmpty) {
      conf.flags.errors.foreach(println)
      println()
      printUsage(conf)
      System.exit(1)
    }

    try {
      new Installer(conf, driver(conf.url)).run()
      println("Installation complete")
    } catch {
      case e: InstallerException =>
        println(e.getMessage)
    }

  }

  private[this] def driver(url: String): Driver = {
    url.trim.split(":").map(_.trim).headOption match {
      case Some(driverName) =>
        drivers.find(_.name == driverName).getOrElse(throw new InstallerException(s"Unknown driver name: '$driverName'"))
      case None =>
        throw new InstallerException("Driver url should start with driver name and a colon. Example: mongo:localhost:12345/dbname")
    }

  }

  private[this] def printUsage(conf: DbInstallerConfig) = {
    println("Database installer utility")
    println("dbi lstfile url [login] [password]")
    print(conf.flags.usageString)
    println("Parameters are accepted via command line or env variables")
    println("Available drivers:")
    drivers.foreach { d =>
      println("  " + d.usageString)
    }

  }

  private[this] def parseArgv(argv: Array[String]): Try[Map[String, String]] = Try {
    val params = Seq("lstFile", "url", "login", "password")

    if (argv.length > params.length) {
      throw new RuntimeException("Too many parameters")
    }

    params.zip(argv).toMap
  }
}
