package org.scalatest.spi.location;

public interface AstNode {
    
    String className();
    
    AstNode parent();
    
    AstNode[] children();
    
    String name();
    
    void addChild(AstNode node);
}
