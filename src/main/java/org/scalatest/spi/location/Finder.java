package org.scalatest.spi.location;

public interface Finder {
    Test find(AstNode node);
}
