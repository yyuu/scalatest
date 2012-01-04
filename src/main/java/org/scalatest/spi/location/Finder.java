package org.scalatest.spi.location;

import scala.Option;

public interface Finder {
    Option<Test> find(AstNode node);
}
