package com.github.treesitter.compiler;

import java.util.Objects;

class Pattern extends Rule{
  private final String value;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pattern pattern = (Pattern) o;
    return Objects.equals(value, pattern.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  Pattern(String value) {
    this.value = value;
  }
}