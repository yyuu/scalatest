package org.scalatest.spi.location;

public class MethodInvocation extends AstNode {
    
    private AstNode target;
    private Object[] args;

    public MethodInvocation(String className, AstNode target, AstNode parent, AstNode children[],
            String name, Object... args) {
        super();
        this.className = className;
        this.target = target;
        this.parent = parent;
        this.children = children;
        this.name = name;
        this.args = args;
    }
    public AstNode getTarget() {
        return target;
    }
    public Object[] getArgs() {
        return args;
    }
}
