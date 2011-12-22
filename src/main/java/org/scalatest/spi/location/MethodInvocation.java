package org.scalatest.spi.location;

public class MethodInvocation extends AstNode {
    
    private AstNode owner;
    private Object[] args;

    public MethodInvocation(String className, AstNode owner, AstNode parent, AstNode children[],
            String name, Object... args) {
        super();
        this.className = className;
        this.owner = owner;
        this.parent = parent;
        this.children = children;
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
