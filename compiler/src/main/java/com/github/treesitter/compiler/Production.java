package com.github.treesitter.compiler;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class Production implements Iterable<ProductionStep>{
  List<ProductionStep> steps;
  int dynamic_precedence;

  Production() {this(Collections.emptyList(), 0);}

  Production(List<ProductionStep> steps, int dynamic_precedence) {
    this.steps = steps;this.dynamic_precedence = dynamic_precedence;
  }

  ProductionStep back() { return steps.get(steps.size()-1); }
  boolean empty() { return steps.isEmpty(); }

  @Override
  public Iterator<ProductionStep> iterator() {
    return steps.iterator();
  }

  int size() { return steps.size(); }
  ProductionStep get(int i) { return steps.get(i); }
  ProductionStep set(int i, ProductionStep step) { return steps.set(i, step); }
}
