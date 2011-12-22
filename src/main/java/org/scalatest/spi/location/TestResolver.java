package org.scalatest.spi.location;

public interface TestResolver {
    Test resolveTest(AstNode node);
}
