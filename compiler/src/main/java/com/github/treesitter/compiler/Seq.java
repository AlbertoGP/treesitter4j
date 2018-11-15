package com.github.treesitter.compiler;

import java.util.Objects;
import java.util.stream.Stream;

class Seq extends Rule {
  private final Rule left;
  private final Rule right;

  @Override
  Stream<Rule> stream() {
    return Stream.of(left, right).flatMap(Rule::stream);
  }

  Seq(Rule left, Rule right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Seq seq = (Seq) o;
    return left.equals(seq.left) &&
            right.equals(seq.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
