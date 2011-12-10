
package org.scalatest.tools

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class SbtCommandParserSpec extends Spec with ShouldMatchers {

  val parser = new SbtCommandParser

  def canParsePhrase(s: String) {
      val result = parser.parseResult(s)
      result match {
        case ns: parser.NoSuccess => fail(ns.toString)
        case _ => 
      }
  }

  def cannotParsePhrase(s: String) {
      val result = parser.parseResult(s)
      result match {
        case parser.Success(result, _) => fail("wasn't supposed to, but parsed: " + result)
        case _ =>
      }
  }

  describe("the cmd terminal?") {
    it("""should parse 'st'""") { canParsePhrase("""st""") }
    it("""should parse 'st --'""") { canParsePhrase("""st --""") }  
    
    // stdout
    it("""should parse 'st -- stdout'""") { canParsePhrase("""st -- stdout""") }
    it("""should parse 'st -- stdout(config="nocolor fullstacks doptestsucceeded")'""") { canParsePhrase("""st -- stdout(config="nocolor fullstacks droptestsucceeded")""") }
    it("""should parse 'st -- stdout (config="nocolor fullstacks doptestsucceeded")'""") { canParsePhrase("""st -- stdout (config="nocolor fullstacks droptestsucceeded")""") }
    it("""should parse 'st -- stdout (config="darkcolor fullstacks doptestsucceeded")'""") { canParsePhrase("""st -- stdout (config="darkcolor fullstacks droptestsucceeded")""") }
    it("""should not parse 'st -- stdout config="nocolor fullstacks droptestsucceeded"'""") { cannotParsePhrase("""st -- stdout config="nocolor fullstacks droptestsucceeded"""") }
    it("""should not parse 'st -- stdout(config="nocolor fullstacks droptestsucceeded"'""") { cannotParsePhrase("""st -- stdout(config="nocolor fullstacks droptestsucceeded"""") }
    it("""should not parse 'st -- stdoutconfig="nocolor fullstacks droptestsucceeded")'""") { cannotParsePhrase("""st -- stdoutconfig="nocolor fullstacks droptestsucceeded")""") }
    it("""should not parse 'st -- stdout(confi="nocolor fullstacks droptestsucceeded")'""") { cannotParsePhrase("""st -- stdout(confi="nocolor fullstacks droptestsucceeded")""") }
    
    // stderr
    it("""should parse 'st -- stderr'""") { canParsePhrase("""st -- stderr""") }
    it("""should parse 'st -- stderr(config="dropinfoprovided dropsuitestarting droptestignored")'""") { canParsePhrase("""st -- stderr(config="dropinfoprovided dropsuitestarting droptestignored")""") }
    
    // stdout + stderr
    it("""should parse 'st -- stdout stderr'""") { canParsePhrase("""st -- stdout stderr""") }
    it("""should parse 'st -- stdout(config="nocolor fullstacks doptestsucceeded") stderr(config="dropinfoprovided dropsuitestarting droptestignored")'""") { canParsePhrase("""st -- stdout(config="nocolor fullstacks droptestsucceeded") stderr(config="dropinfoprovided dropsuitestarting droptestignored")""") }
    
    // include
    it("""should parse 'st -- include("org.scala.a", "org.scala.b", "org.scala.c")'""") { canParsePhrase("""st -- include("org.scala.a", "org.scala.b", "org.scala.c")""") }
    it("""should parse 'st -- include ("org.scala.a", "org.scala.b", "org.scala.c")'""") { canParsePhrase("""st -- include ("org.scala.a", "org.scala.b", "org.scala.c")""") }
    it("""should parse 'st -- include(org.scala.a, org.scala.b, org.scala.c)'""") { canParsePhrase("""st -- include(org.scala.a, org.scala.b, org.scala.c)""") }
    it("""should not parse 'st -- include'""") { cannotParsePhrase("""st -- include""") }
    it("""should not parse 'st -- include("org.scala.a", "org.scala.b", "org.scala.c"'""") { cannotParsePhrase("""st -- include("org.scala.a", "org.scala.b", "org.scala.c"""") }
    it("""should not parse 'st -- include"org.scala.a", "org.scala.b", "org.scala.c")'""") { cannotParsePhrase("""st -- include"org.scala.a", "org.scala.b", "org.scala.c")""") }
    it("""should not parse 'st -- include "org.scala.a", "org.scala.b", "org.scala.c"'""") { cannotParsePhrase("""st -- include "org.scala.a", "org.scala.b", "org.scala.c"""") }
    
    // exclude
    it("""should parse 'st -- exclude("org.scala.a", "org.scala.b", "org.scala.c")'""") { canParsePhrase("""st -- exclude("org.scala.a", "org.scala.b", "org.scala.c")""") }
    it("""should parse 'st -- exclude ("org.scala.a", "org.scala.b", "org.scala.c")'""") { canParsePhrase("""st -- exclude ("org.scala.a", "org.scala.b", "org.scala.c")""") }
    it("""should parse 'st -- exclude(org.scala.a, org.scala.b, org.scala.c)'""") { canParsePhrase("""st -- exclude(org.scala.a, org.scala.b, org.scala.c)""") }
    
    // concurrent
    it("""should parse 'st -- concurrent'""") { canParsePhrase("""st -- concurrent""") }
    it("""should not parse 'st -- concurrnt'""") { cannotParsePhrase("""st -- concurrnt""") }
    
    // membersonly
    it("""should parse 'st -- membersonly("a.b.c")'""") { canParsePhrase("""st -- membersonly("a.b.c")""") }
    it("""should parse 'st -- membersonly(a.b.c)'""") { canParsePhrase("""st -- membersonly(a.b.c)""") }
    it("""should parse 'st -- membersonly("a.b.c", "a.b.d", "a.b.e")'""") { canParsePhrase("""st -- membersonly("a.b.c", "a.b.d", "a.b.e")""") }
    it("""should parse 'st -- membersonly(a.b.c, a.b.d, a.b.e)'""") { canParsePhrase("""st -- membersonly(a.b.c, a.b.d, a.b.e)""") }
    it("""should parse 'st -- membersonly(a.b.c, "a.b.d", a.b.e)'""") { canParsePhrase("""st -- membersonly(a.b.c, "a.b.d", a.b.e)""") }
    it("""should not parse 'st -- membersonly'""") { cannotParsePhrase("""st -- membersonly""") }
    
    // wildcard
    it("""should parse 'st -- wildcard("a.b.c")'""") { canParsePhrase("""st -- wildcard("a.b.c")""") }
    it("""should parse 'st -- wildcard(a.b.c)'""") { canParsePhrase("""st -- wildcard(a.b.c)""") }
    it("""should parse 'st -- wildcard("a.b.c", "a.b.d", "a.b.e")'""") { canParsePhrase("""st -- wildcard("a.b.c", "a.b.d", "a.b.e")""") }
    it("""should parse 'st -- wildcard(a.b.c, a.b.d, a.b.e)'""") { canParsePhrase("""st -- wildcard(a.b.c, a.b.d, a.b.e)""") }
    it("""should parse 'st -- wildcard(a.b.c, "a.b.d", a.b.e)'""") { canParsePhrase("""st -- wildcard(a.b.c, "a.b.d", a.b.e)""") }
    it("""should not parse 'st -- wildcard'""") { cannotParsePhrase("""st -- wildcard""") }
    
    // suite
    it("""should parse 'st a.b.c'""") { canParsePhrase("""st a.b.c""") }
    it("""should parse 'st a.b.c a.b.d a.b.e'""") { canParsePhrase("""st a.b.c a.b.d a.b.e""") }
    it("""should parse 'st "a.b.c"'""") { canParsePhrase("""st "a.b.c"""") }
    it("""should parse 'st "a.b.c" "a.b.d" "a.b.e"'""") { canParsePhrase("""st "a.b.c" "a.b.d" "a.b.e"""") }
    it("""should parse 'st a.b.c -- stdout'""") { canParsePhrase("""st a.b.c -- stdout""") }
    it("""should parse 'st a.b.c a.b.d a.b.e -- stdout'""") { canParsePhrase("""st a.b.c a.b.d a.b.e -- stdout""") }
    
    // junit
    it("""should parse 'st -- junit("a.b.c")'""") { canParsePhrase("""st -- junit("a.b.c")""") }
    it("""should parse 'st -- junit(a.b.c)'""") { canParsePhrase("""st -- junit(a.b.c)""") }
    it("""should parse 'st -- junit("a.b.c", "a.b.d", "a.b.e")'""") { canParsePhrase("""st -- junit("a.b.c", "a.b.d", "a.b.e")""") }
    it("""should parse 'st -- junit(a.b.c, a.b.d, a.b.e)'""") { canParsePhrase("""st -- junit(a.b.c, a.b.d, a.b.e)""") }
    it("""should parse 'st -- junit(a.b.c, "a.b.d", a.b.e)'""") { canParsePhrase("""st -- junit(a.b.c, "a.b.d", a.b.e)""") }
    it("""should not parse 'st -- junit'""") { cannotParsePhrase("""st -- junit""") }
    
    // testng
    it("""should parse 'st -- testng("a.b.c")'""") { canParsePhrase("""st -- testng("a.b.c")""") }
    it("""should parse 'st -- testng(a.b.c)'""") { canParsePhrase("""st -- testng(a.b.c)""") }
    it("""should parse 'st -- testng("a.b.c", "a.b.d", "a.b.e")'""") { canParsePhrase("""st -- testng("a.b.c", "a.b.d", "a.b.e")""") }
    it("""should parse 'st -- testng(a.b.c, a.b.d, a.b.e)'""") { canParsePhrase("""st -- testng(a.b.c, a.b.d, a.b.e)""") }
    it("""should parse 'st -- testng(a.b.c, "a.b.d", a.b.e)'""") { canParsePhrase("""st -- testng(a.b.c, "a.b.d", a.b.e)""") }
    it("""should not parse 'st -- testng'""") { cannotParsePhrase("""st -- testng""") }
    
    // graphic
    it("""should parse 'st -- graphic'""") { canParsePhrase("""st -- graphic""") }
    it("""should parse 'st -- graphic(config="dropinfoprovided dropsuitestarting droptestignored")'""") { canParsePhrase("""st -- graphic(config="dropinfoprovided dropsuitestarting droptestignored")""") }
    it("""should not parse 'st -- graphic(config="nocolor")'""") { cannotParsePhrase("""st -- graphic(config="nocolor")""") }
    it("""should not parse 'st -- graphic(config="shortstacks")'""") { cannotParsePhrase("""st -- graphic(config="shortstacks")""") }
    it("""should not parse 'st -- graphic(config="fullstacks")'""") { cannotParsePhrase("""st -- graphic(config="fullstacks")""") }
    it("""should not parse 'st -- graphic(config="durations")'""") { cannotParsePhrase("""st -- graphic(config="durations")""") }
    
    // file
    it("""should parse 'st -- file(filename="test.xml")'""") { canParsePhrase("""st -- file(filename="test.xml")""") }
    it("""should parse 'st -- file(filename="test.xml", config="durations shortstacks dropteststarting")'""") { canParsePhrase("""st -- file(filename="test.xml", config="durations shortstacks dropteststarting")""") }
    
    // junitxml
    it("""should parse 'st -- junitxml(directory="test")'""") { canParsePhrase("""st -- junitxml(directory="test")""") }
    it("""should parse 'st -- junitxml (directory="test")'""") { canParsePhrase("""st -- junitxml (directory="test")""") }
    it("""should not parse 'st -- junitxml directory="test""'""") { cannotParsePhrase("""st -- junitxml directory="test""""") }
    it("""should not parse 'st -- junitxml'""") { cannotParsePhrase("""st -- junitxml""") }
    it("""should not parse 'st -- junitxml(directory="test"'""") { cannotParsePhrase("""st -- junitxml(directory="test"""") }
    it("""should not parse 'st -- junitxmldirectory="test")'""") { cannotParsePhrase("""st -- junitxmldirectory="test")""") }
    it("""should not parse 'st -- junitxml(director="test")'""") { cannotParsePhrase("""st -- junitxml(director="test")""") }
    
    // dashboard
    it("""should parse 'st -- dashboard(directory="test")'""") { canParsePhrase("""st -- dashboard(directory="test")""") }
    it("""should parse 'st -- dashboard(directory="test", archive="5")'""") { canParsePhrase("""st -- dashboard(directory="test", archive="5")""") }
    it("""should not parse 'st -- dashboard()'""") { cannotParsePhrase("""st -- dashboard()""") }
    it("""should not parse 'st -- dashboard(directory="test", archive="")'""") { cannotParsePhrase("""st -- dashboard(directory="test", archive="")""") }
    
    // html
    it("""should parse 'st -- html(filename="test.html")'""") { canParsePhrase("""st -- html(filename="test.html")""") }
    it("""should parse 'st -- html(filename="test.html", config="nocolor fullstacks durations")'""") { canParsePhrase("""st -- html(filename="test.html", config="nocolor fullstacks durations")""") }
    it("""should not parse 'st -- html'""") { cannotParsePhrase("""st -- html""") }
    
    // reporterclass
    it("""should parse 'st -- reporterclass(classname="a.b.c")'""") { canParsePhrase("""st -- reporterclass(classname="a.b.c")""") }
    it("""should parse 'st -- reporterclass(classname="a.b.c", config="dropsuitestarting dropinfoprovided dropteststarting")'""") { canParsePhrase("""st -- reporterclass(classname="a.b.c", config="dropsuitestarting dropinfoprovided dropteststarting")""") }
    it("""should not parse 'st -- reporterclass'""") { cannotParsePhrase("""st -- reporterclass'""") }
    it("""should not parse 'st -- reporterclass(classname="a.b.c", config="nocolor")""") { cannotParsePhrase("""st -- reporterclass(classname="a.b.c", config="nocolor")""") }
    it("""should not parse 'st -- reporterclass(classname="a.b.c", config="shortstacks")""") { cannotParsePhrase("""st -- reporterclass(classname="a.b.c", config="shortstacks")""") }
    it("""should not parse 'st -- reporterclass(classname="a.b.c", config="fullstacks")""") { cannotParsePhrase("""st -- reporterclass(classname="a.b.c", config="fullstacks")""") }
    it("""should not parse 'st -- reporterclass(classname="a.b.c", config="durations")""") { cannotParsePhrase("""st -- reporterclass(classname="a.b.c", config="durations")""") }
  }
}

