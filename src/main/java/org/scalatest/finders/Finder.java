package org.scalatest.finders;

import scala.Option;

public interface Finder {
    Option<Selection> find(AstNode node);
}
