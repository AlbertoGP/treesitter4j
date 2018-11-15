package com.github.treesitter.compiler;

class Symbol extends Rule{
  enum Type {
    External,
    Terminal,
    NonTerminal,
  }public static final Symbol END_OF_INPUT = new Symbol(-1, Type.Terminal);
public static final  Symbol NONE = new Symbol(-3, null);
public static final Symbol START = new Symbol(-2, Type.NonTerminal);
private final int index;
  private final Type type;

  Symbol(int index, Type type) {
    this.index = index;
    this.type = type;
  }

  boolean isBuiltIn() {
    return index < 0;
  }

}
