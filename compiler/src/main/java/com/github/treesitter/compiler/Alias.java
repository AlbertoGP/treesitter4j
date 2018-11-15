package com.github.treesitter.compiler;

class Alias implements Comparable<Alias>{
  String value = "";
  boolean is_named = false;

  @Override
  public int compareTo(Alias alias) {
    final var result= value.compareTo(alias.value);
    return result == 0 ? Boolean.compare(is_named, alias.is_named): result;
  }
}
