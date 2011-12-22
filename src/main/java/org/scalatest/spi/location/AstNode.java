package org.scalatest.spi.location;

public abstract class AstNode {
    protected String className;
    protected AstNode parent;
    protected AstNode[] children;
    protected String name;
    
    public String getClassName() {
        return className;
    }
    public AstNode getParent() {
        return parent;
    }
    public AstNode[] getChildren() {
        return children;
    }
    public String getName() {
        return name;
    }
}
