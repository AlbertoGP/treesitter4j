package com.github.treesitter.compiler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class Rule {
private static final Rule BLANK = new Rule(){};

Stream<Rule> stream() {
  return Stream.of(this);
  }

static Rule choice(List<Rule> rules) {
  List<Rule> elements = rules.stream().flatMap(Rule::stream).collect(Collectors.toList());
  return (elements.size() == 1) ? elements.get(0) : new Choice(elements);
}

Rule repeat() {
  return new Repeat(this);
}

static Rule seq(Stream<Rule> rules) {
  return rules.reduce(Seq::new).orElse(BLANK);
}

}
