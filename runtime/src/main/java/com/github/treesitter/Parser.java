package com.github.treesitter;

import java.io.PrintWriter;
import java.util.List;

public class Parser<T extends Symbol> {

  public Parser(Language<T> language, int operationLimit) {}

  public Language<T> language();

  public void printDotGraph(PrintWriter output);

  public void haltOnError(boolean halt);

  public Tree<T> parse(Tree<T> tree, Input input);

  public List<Range> includedRanges();
}
