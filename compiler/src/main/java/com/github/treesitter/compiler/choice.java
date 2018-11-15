package com.github.treesitter.compiler;

import java.util.List;
import java.util.Objects;

class Choice extends Rule{
  private final List<Rule> elements;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Choice choice = (Choice) o;
    return Objects.equals(elements, choice.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

  Choice(List<Rule> elements) {
    this.elements = elements;
  }
}
