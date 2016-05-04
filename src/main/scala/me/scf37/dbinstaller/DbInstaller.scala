package me.scf37.dbinstaller

/**
  * Created by asm on 04.05.16.
  */
object DbInstaller {

  private[dbinstaller] def helloMessage = {
    val hello = "Hello"
    val world = "World"
    Seq(hello, world).mkString(", ") + "!"
  }

  def main(args: Array[String]) = {
    println(helloMessage)
  }
}
