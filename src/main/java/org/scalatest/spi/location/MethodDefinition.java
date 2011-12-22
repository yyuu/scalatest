package org.scalatest.spi.location;

public class MethodDefinition extends AstNode {
    
    private Class[] paramTypes;
    
    public MethodDefinition(String className, AstNode parent, AstNode[] children,
            String name, Class... paramTypes) {
        super();
        this.className = className;
        this.parent = parent;
        this.children = children;
        this.name = name;
        this.paramTypes = paramTypes;
    }
    public Class[] getParamTypes() {
        return paramTypes;
    }
}
