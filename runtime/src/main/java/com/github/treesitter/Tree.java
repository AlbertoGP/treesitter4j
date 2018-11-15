package com.github.treesitter;

import java.io.PrintWriter;
import java.util.stream.Stream;

public class Tree<T extends Symbol> {
  private Subtree<T> root;
  private final Language<T> language;

  Tree(Subtree<T> root, Language<T> language) {
    super();
    this.root = root;
    this.language = language;
  }

  public Node<T> root() {
    return new Node<T>(this, root, root.padding(), null);
  }

  public void edit(InputEdit edit) {
    root = root.edit(edit);
  }

  public Stream<Range> changedRanges();

  public void printDotGraph(PrintWriter output) {
    root.printDotGraph(language, output);
  }

  public Language<T> language() {
    return language;
  }
}
