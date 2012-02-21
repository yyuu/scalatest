package org.scalatest.finders;

public interface AstNode {
    
    String className();
    
    AstNode parent();
    
    AstNode[] children();
    
    String name();
    
    void addChild(AstNode node);
}
