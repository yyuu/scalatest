package org.scalatest.tools

import scala.util.parsing.combinator.JavaTokenParsers

class SbtCommandParser extends JavaTokenParsers {

  def parseCommand(command: String) {
    val result = parseResult(command)
    result match {
      case Success(tree, _) => println("success: " + tree)
      case e: NoSuccess => {
        Console.err.println(e)
      }
    }
  }
  
  def parseResult(command: String) = {
    parseAll(cmd, command)
  }

  def cmd: Parser[Any] = "st" ~ opt(dashArgs)

  def dashArgs: Parser[Any] = "--" ~ opt(stArgs)

  def stArgs: Parser[Any] = rep(stArgsOpt)

  def stArgsOpt: Parser[Any] = include | 
                              exclude | 
                              concurrent | 
                              membersonly | 
                              wildcard | 
                              suite | 
                              junit | 
                              testng | 
                              stdout |
                              stderr |
                              graphic | 
                              file | 
                              junitxml | 
                              dashboard | 
                              html | 
                              reporterclass

  def include: Parser[Any] = "include" ~ list
  def exclude: Parser[Any] = "exclude" ~ list
 
  def concurrent: Parser[Any] = "concurrent"
  def membersonly: Parser[Any] = "membersonly" ~ list
  def wildcard: Parser[Any] = "wildcard" ~ list
  def suite: Parser[Any] = "suite" ~ list
  def junit: Parser[Any] = "junit" ~ list
  def testng: Parser[Any] = "testng" ~ list
  
  def list: Parser[Any] = "(" ~> repsep(stringLiteral, ",") <~ ")"
 
  def stdout: Parser[Any] = "stdout" ~ opt("(" ~ config ~ ")")
  def stderr: Parser[Any] = "stderr" ~ opt("(" ~ config ~ ")")
  
  def graphic: Parser[Any] = "graphic" ~ opt("(" ~ limitedConfig ~ ")")
  def file: Parser[Any] = "file" ~ "(" ~ "filename" ~ "=" ~ stringLiteral ~ opt("," ~ config) ~ ")"
  def junitxml: Parser[Any] = "junitxml" ~ "(" ~ "directory" ~ "=" ~ stringLiteral ~ ")"
  def dashboard: Parser[Any] = "dashboard" ~ "(" ~ "directory" ~ "=" ~ stringLiteral ~ opt("," ~ archive) ~ ")"
  def html: Parser[Any] = "html" ~ "(" ~ "filename" ~ "=" ~ stringLiteral ~ opt("," ~ config) ~ ")"
  def reporterclass: Parser[Any] = "reporterclass" ~ "(" ~ "classname" ~ "=" ~ stringLiteral ~ opt("," ~ limitedConfig) ~ ")"

  def archive: Parser[Any] = "archive" ~ "=" ~ "\"" ~ wholeNumber ~ "\""  

  def config: Parser[Any] = "config" ~ "=" ~ "\"" ~ rep(configOpt) ~ "\""
  
  def configOpt: Parser[Any] = "dropteststarting" | 
                           "droptestsucceeded" | 
                           "droptestignored" | 
                           "droptestpending" | 
                           "dropsuitestarting" | 
                           "dropsuitecompleted" | 
                           "dropinfoprovided" | 
                           "nocolor" | 
                           "shortstacks" | 
                           "fullstacks" | 
                           "durations"
  
  def limitedConfig: Parser[Any] = "config" ~ "=" ~ "\"" ~ rep(limitedConfigOpt) ~ "\""
                           
  def limitedConfigOpt: Parser[Any] = "dropteststarting" | 
                                  "droptestsucceeded" | 
                                  "droptestignored" | 
                                  "droptestpending" | 
                                  "dropsuitestarting" | 
                                  "dropsuitecompleted" | 
                                  "dropinfoprovided"
}

object SbtCommandParser {
  def main(args: Array[String]) {
    
    (new SbtCommandParser).parseCommand("""st""")
/*
    (new SbtCommandParser).parseCommand("""st include("a", "b", "c")""")
    (new SbtCommandParser).parseCommand("""st exclude("a", "b", "c")""")
    (new SbtCommandParser).parseCommand("""st exclude("a", "b", "c") concurrent""")
    (new SbtCommandParser).parseCommand("""st membersonly("a", "b", "c") stdout""")
    (new SbtCommandParser).parseCommand("""st wildcard("a", "b", "c") stdout(config = "dropteststarting droptestpending")""")
*/
  }
}

