package com.github.treesitter.compiler;

import java.util.Objects;

class Repeat extends Rule{
  private final Rule rule;

  Repeat(com.github.treesitter.compiler.Rule rule) {
    this.rule = rule;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Repeat repeat = (Repeat) o;
    return rule.equals(repeat.rule);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hash(rule);
  }

  @Override
  Rule repeat() {
    return this;
  }
}

