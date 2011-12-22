package org.scalatest.spi.location;

public class MethodFinder implements Finder {

    public Test find(AstNode node) {
        if(node instanceof MethodDefinition) {
            MethodDefinition methodDef = (MethodDefinition) node;
            if(methodDef.getParamTypes().length == 0)
                return new Test(methodDef.getClassName(), methodDef.getClassName() + "." + methodDef.getName(), new String[] { methodDef.getName() });
            else
                return null;
        }
        else
            return null;
    }

}
