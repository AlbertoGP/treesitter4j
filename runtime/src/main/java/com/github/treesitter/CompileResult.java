package com.github.treesitter;

import java.util.function.Consumer;

public class CompileResult {
  private final String code;
  private final String errorMessage;
  private final CompilerError error_type;

  public static CompileResult ts_compile_grammar(String input, Consumer<String> log_file);

  public static CompileResult ts_compile_property_sheet(String input, Consumer<String> log_file);
}
