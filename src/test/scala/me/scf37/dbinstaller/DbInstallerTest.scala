package me.scf37.dbinstaller

import org.scalatest.FunSuite

class DbInstallerTest extends FunSuite {
  test("check that we greet the world properly!") {
    assert(DbInstaller.helloMessage == "Hello, World!")
  }
}
