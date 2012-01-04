package org.scalatest.spi.location;

import scala.Option;

public interface Finder {
    Option<Selection> find(AstNode node);
}
