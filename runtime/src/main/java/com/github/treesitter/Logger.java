package com.github.treesitter;

public interface Logger {
  void logParse(String message);

  void logLex(String message, int character);
}
