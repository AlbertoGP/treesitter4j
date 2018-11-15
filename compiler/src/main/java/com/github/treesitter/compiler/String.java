package com.github.treesitter.compiler;

import java.util.Objects;

class String extends Rule{
  private final String value;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    String string = (String) o;
    return value.equals(string.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  String(String value) {
    this.value = value;
  }
}