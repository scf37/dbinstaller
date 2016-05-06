package me.scf37.dbinstaller.driver

import java.time.Instant

import me.scf37.dbinstaller.api.InstallerException
import me.scf37.dbinstaller.api.ScriptEntry
import me.scf37.dbinstaller.config.DbInstallerConfig
import org.scalatest.FunSuite

/**
  * Created by asm on 07.05.16.
  */
class MongoDriverTest extends FunSuite {
  test("test url parsing - positive") {
    val d = new MongoDriver()

    d.open(new DbInstallerConfig(Map("url" -> "mongo:host/db")))
    assert(d.host == "host")
    assert(d.db == "db")
    assert(d.port > 0)

    d.close()

    d.open(new DbInstallerConfig(Map("url" -> "mongo:host2:12344/db2")))
    assert(d.host == "host2")
    assert(d.db == "db2")
    assert(d.port == 12344)

    d.close()
  }

  test("test url parsing - negative") {
    val d = new MongoDriver()

    intercept[InstallerException] {
      d.open(new DbInstallerConfig(Map("url" -> "host:12345/db")))
    }

    intercept[InstallerException] {
      d.open(new DbInstallerConfig(Map("url" -> "mongo:hostdb")))
    }

    intercept[InstallerException] {
      d.open(new DbInstallerConfig(Map("url" -> "mongo:host:port")))
    }

    intercept[InstallerException] {
      d.open(new DbInstallerConfig(Map("url" -> "mongo:host:12345")))
    }
  }

  //only for local run - requires local mongodb instance
  ignore("integration test") {
    val script = s"test-${System.nanoTime()}"
    val d = new MongoDriver()

    d.open(new DbInstallerConfig(Map("url" -> "mongo:localhost/test")))

    assert(d.getLatestEntry(script).isEmpty)

    d.addEntry(ScriptEntry(name=script, version=0, date=Instant.now(), contents="contents", hash="hash"))

    val e = d.getLatestEntry(script)

    assert(e.isDefined)
    assert(e.get.version == 0)
    assert(e.get.hash == "hash")

    d.addEntry(ScriptEntry(name=script, version=1, date=Instant.now(), contents="contents2", hash="hash2"))

    val ee = d.getLatestEntry(script)

    assert(ee.isDefined)
    assert(ee.get.version == 1)
    assert(ee.get.hash == "hash2")

  }
}
