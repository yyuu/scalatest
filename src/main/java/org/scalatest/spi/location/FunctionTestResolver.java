package org.scalatest.spi.location;

public class FunctionTestResolver implements TestResolver {
    
    public Test resolveTest(AstNode node) {
        if (node instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) node;
            if(invocation.getName().equals("test") && invocation.getArgs().length == 1 && invocation.getArgs()[0].getClass() == String.class)
                return new Test(invocation.getClassName(), invocation.getClassName() + ": \"" + invocation.getArgs()[0].toString() + "\"", new String[] { invocation.getArgs()[0].toString() });
            else
                return null;
        }
        else
            return null;
    }
}
