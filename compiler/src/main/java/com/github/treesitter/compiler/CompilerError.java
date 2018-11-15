package com.github.treesitter.compiler;

import java.util.Objects;

public class CompileError {

  public CompileError(CompileErrorType type, String message) {
     this.type = type;this.message = message;
  }

  public static CompileError none() {
    return CompileError(CompileErrorType.NONE, "");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompileError that = (CompileError) o;
    return type.equals(that.type) &&
            message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, message);
  }

  boolean bad() {
    return type != CompileErrorType.NONE;
  }

  private final CompileErrorType type;
  private final String message;
}
