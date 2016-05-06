package me.scf37.dbinstaller.api

import scala.util.control.NoStackTrace

/**
  * Exception for displaying error messages to the user
  *
  * @param msg
  */
class InstallerException(msg: String) extends RuntimeException(msg) with NoStackTrace
