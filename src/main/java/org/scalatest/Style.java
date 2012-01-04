package org.scalatest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.scalatest.spi.location.Finder;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Style {
    Class<? extends Finder> value();
}