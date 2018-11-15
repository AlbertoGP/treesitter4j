package com.github.treesitter.compiler;

import java.util.Objects;

class MetadataParams {
  Alias alias;
  Associativity associativity = Associativity.NONE;
  int dynamic_precedence;
  boolean has_associativity;
  boolean has_precedence;
  boolean is_active;
  boolean is_main_token;
  boolean is_string;
  boolean is_token;
  int precedence;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MetadataParams that = (MetadataParams) o;
    return precedence == that.precedence &&
            dynamic_precedence == that.dynamic_precedence &&
            has_precedence == that.has_precedence &&
            has_associativity == that.has_associativity &&
            is_token == that.is_token &&
            is_string == that.is_string &&
            is_active == that.is_active &&
            is_main_token == that.is_main_token &&
            associativity == that.associativity &&
            alias.equals(that.alias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(precedence, dynamic_precedence, associativity, has_precedence, has_associativity, is_token, is_string, is_active, is_main_token, alias);
  }
}
