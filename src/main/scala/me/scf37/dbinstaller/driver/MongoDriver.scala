package me.scf37.dbinstaller.driver

import java.io.File
import java.time.Instant
import java.util.Collections
import java.util.Date
import java.util.regex.Pattern

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import me.scf37.dbinstaller.api.Driver
import me.scf37.dbinstaller.api.InstallerException
import me.scf37.dbinstaller.api.ScriptEntry
import me.scf37.dbinstaller.config.DbInstallerConfig
import org.bson.Document

/**
  * Created by asm on 07.05.16.
  */
class MongoDriver extends Driver {
  var client: MongoDatabase = null
  var host: String = _
  var port: Int = _
  var db: String = _
  var user: String = _
  var password: String = _

  /**
    * @return driver name AKA driver url prefix
    */
  override def name: String = "mongo"

  /**
    * @return usage help
    */
  override def usageString: String = "mongo:[host]:[port]/[database] or mongo:[host]/[database] MongoDB driver."

  /**
    * Add new entry
    */
  override def addEntry(entry: ScriptEntry): Unit = {
    val doc = new Document("name", entry.name)
      .append("version", entry.version)
      .append("date", new Date(entry.date.toEpochMilli))
      .append("hash", entry.hash)
      .append("contents", entry.contents)

    client.getCollection("dbscripts").insertOne(doc)
  }

  /**
    * Get latest (i.e. with maximum version) entry of this script
    * @return script entry if any or None
    */
  override def getLatestEntry(scriptName: String): Option[ScriptEntry] = {
    val doc = Option(client.getCollection("dbscripts").find(new Document("name", scriptName))
      .sort(new Document("version", -1)).first())

    doc.map { d =>
      ScriptEntry(
        name = d.getString("name"),
        version = d.getLong("version"),
        date = Instant.ofEpochMilli(d.getDate("date").getTime),
        hash = d.getString("hash"),
        contents = d.getString("contents")
      )
    }
  }

  /**
    * Close this driver and release used resources
    */
  override def close(): Unit = {
    if (client != null) {
      client = null
    }
  }

  /**
    * Execute script by given file name against database
    */
  override def exec(script: File): Unit = {
    import scala.sys.process._

    val cmd = if (user.isEmpty)
      s"mongo $host:$port/$db ${script}"
    else
      s"mongo $host:$port/$db -u $user -p $password ${script}"

    val code = cmd.!
    if (code != 0) throw new InstallerException(s"Execution failed with code $code")
  }

  /**
    * initialize this driver instance with provided config
    * @param config
    */
  override def open(config: DbInstallerConfig): Unit = {
    parseClient(config)

    client = if (user.isEmpty) {
      new MongoClient(new ServerAddress(host, port)).getDatabase(db)
    } else {
      new MongoClient(new ServerAddress(host, port),
        Collections.singletonList(MongoCredential.createScramSha1Credential(user, db, password.toCharArray))).getDatabase(db)
    }
}

  def parseClient(config: DbInstallerConfig): Unit = {
    val url = {
      val u = config.url.trim
      if (!u.startsWith("mongo:")) {
        throw new InstallerException(s"Invalid url '$u'. Mongo url must start with mongo: prefix")
      } else u.substring("mongo:".length).trim
    }

    def m(pattern: String, source: String): List[String] = {
      val m = Pattern.compile(pattern).matcher(source)
      if (m.matches()) {
        1.to(m.groupCount()).map(i => m.group(i)).toList
      } else Nil

    }

    // mongo:[host]:[port]/[database] form
    m("(.+):(\\d+)/(.+)", url) match {
      case host :: port :: db :: Nil =>
        mkClient(host, port.toInt, db, config.login, config.password)
        return
      case _ =>
    }

    // mongo:[host]/[database] form
    m("(.+)/(.+)", url) match {
      case host :: db :: Nil =>
        mkClient(host, 27017, db, config.login, config.password)
        return
      case _ =>
    }

    throw new InstallerException(s"Invalid url format: '${config.url}'. Expected mongo:[host]:[port]/[database] or mongo:[host]/[database]")
  }

  def mkClient(host: String, port: Int, db: String, user: String, password: String): Unit = {
    this.host = host
    this.port = port
    this.db = db
    this.user = user
    this.password = password
  }
}
