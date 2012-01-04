package org.scalatest.spi.location;

public abstract class AstNode {
    protected String className;
    protected AstNode parent;
    protected AstNode[] children;
    protected String name;
    
    public AstNode(String className, AstNode parent, AstNode[] children, String name) {
        this.className = className;
        this.parent = parent;
        this.children = children;
        this.name = name;
        
        fillAstNodeParent(this);
    }
    
    public String className() {
        return className;
    }
    public AstNode parent() {
        return parent;
    }
    public AstNode[] children() {
        return children;
    }
    public String name() {
        return name;
    }
    
    private void fillAstNodeParent(AstNode node) {
        AstNode[] children = node.children();
        for(AstNode child : children) 
            child.parent = node;
        for(AstNode child : children)
            fillAstNodeParent(child);
    }
}
