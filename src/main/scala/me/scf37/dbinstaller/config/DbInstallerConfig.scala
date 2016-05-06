package me.scf37.dbinstaller.config

import java.nio.file.Paths

import me.scf37.config2.Config
import me.scf37.config2.Flags

/**
  * Created by asm on 06.05.16.
  */
class DbInstallerConfig(args: Map[String, String]) {
  val source = Config.from(System.getenv())
    .overrideWith(args)

  val flags = new Flags(source.properties())

  val lstFile: String = flags("lstFile", "path to lst file")(identity)
  val url: String = flags("url", "database driver url, in form of [driver name]:[driver-specific db url]")(identity)
  val login: String = flags("login", "login name to use to connect to the database", Some(""))(identity)
  val password: String = flags("password", "password to use to connect to the database", Some(""))(identity)

  val workDir = Option(System.getenv("WORKDIR")).map(Paths.get(_))
    .getOrElse(Paths.get(".")).toAbsolutePath.normalize()
}
