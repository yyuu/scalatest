package org.scalatest.spi.location;

public class MethodInvocation extends AstNode {
    
    private AstNode owner;
    private Object[] args;

    public MethodInvocation(String className, AstNode parent, AstNode children[],
            AstNode owner, String name, Object... args) {
        super();
        this.className = className;
        this.parent = parent;
        this.children = children;
        this.owner = owner;
        this.name = name;
        this.args = args;
    }
    public AstNode getOwner() {
        return owner;
    }
    public Object[] getArgs() {
        return args;
    }
}
