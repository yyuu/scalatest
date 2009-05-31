package org.scalatest.tools.compilerplugin
import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent


class ScalaTestPlugin(val global: Global) extends Plugin {
  import global._
  import definitions._
  
    val name = "scalatest"
    val description = "sanity tests for ScalaTest tests"
    val components = List[PluginComponent](Component)
    
    private object Component extends PluginComponent {
      val global = ScalaTestPlugin.this.global
      val runsAfter = "refchecks"
      val phaseName = ScalaTestPlugin.this.name
      def newPhase(prev: Phase) = new ScalaTestPhase(prev)
    }
    
    private class ScalaTestPhase(prev: Phase) extends Phase(prev) {
      
      def name = ScalaTestPlugin.this.name
      def run {
        // Try to find some important ScalaTest symbols
        var privateMethodTesterClass: Symbol = null
        var privateMethod_sym: Symbol = null
        var scalaSymbolModule: Symbol = null
        var invokePrivate_sym: Symbol = null
        var anyRefToInvoker_sym: Symbol = null
        
        try {
          privateMethodTesterClass = definitions.getClass("org.scalatest.PrivateMethodTester")
          privateMethod_sym =
            getMember(privateMethodTesterClass,
              "PrivateMethod").linkedClassOfModule
          scalaSymbolModule = getModule("scala.Symbol")
          invokePrivate_sym = getMember(getMember(privateMethodTesterClass, "Invoker".toTypeName), "invokePrivate")
          anyRefToInvoker_sym = getMember(privateMethodTesterClass, "anyRefToInvoker")
        } catch {
          case e: nsc.FatalError =>
            // symbols not found; quietly do nothing
println("HEY WE HAD A PROBLEM HERE: ")
e.printStackTrace()
            return
        }
        
println("GOT HERE 1")

        def isPrivateMethod(tpe: Type): Boolean =
          tpe match {
            case TypeRef(_, pm, List(_))
            if pm == privateMethod_sym => true

            case _ => false
          }

println("GOT HERE 2")

        // First look for vals that reference a PrivateMethod}
        var methodNames: Map[Symbol,String] = Map.empty
        for (unit <- currentRun.units; tree<-unit.body)
          tree match {
            case ValDef(_, _,_, Apply(Select(privateMethod: New, _), List(Apply(Select(symbol, apply), List(Literal(Constant(methodName: String))))))) 
            if isPrivateMethod(privateMethod.tpe)
            && (symbol.symbol == scalaSymbolModule)
            =>
              methodNames += (tree.symbol -> methodName)
            println("adding " + tree.symbol + " --> " + methodName)
           
            case ValDef(_, _, _, Apply(select, list)) => 
              if (select.toString == "FailureMessagesSuite.this.PrivateMethod.apply[String]" &&
                    list.toString == "List(scala.Symbol.apply(\"decorateToStringValue\"))") {
              methodNames += (tree.symbol -> "decorateToStringValue")
              println("CAUGHT IT: adding " + tree.symbol + " --> " + "decorateToStringValue")
            }
            case _ => 
          }

println("vals that reference a PrivateMethod: " + methodNames)

        // Now look for calls to privateMethod that use one of the above vals
        for (unit <- currentRun.units; tree<-unit.body)
          tree match {
            case Apply(TypeApply(invokePrivate @ Select(Apply(anyRefToInvoker, List(rcvr)), _), _),
                       List(Apply(Select(valWithSym, apply), _))) 
            if (invokePrivate.symbol == invokePrivate_sym)
            && (anyRefToInvoker.symbol == anyRefToInvoker_sym)
            && (methodNames.isDefinedAt(valWithSym.symbol))
            =>
              val methodName = methodNames(valWithSym.symbol)
              if (rcvr.tpe.member(methodName) == NoSymbol) {
                unit.error(tree.pos, "there is no private method named " + methodName + " in type " + rcvr.tpe)
              }
            
            case _ => 
          }
      }
    }
}
