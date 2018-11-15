package com.github.treesitter;

public class InputEdit {
  public int startOffset() {
    return startOffset;
  }

  public int oldEndOffset() {
    return oldEndOffset;
  }

  public int newEndOffset() {
    return newEndOffset;
  }

  public Point startPoint() {
    return startPoint;
  }

  public Point oldEndPoint() {
    return oldEndPoint;
  }

  public Point newEndPoint() {
    return newEndPoint;
  }

  private final int startOffset;

  private final int oldEndOffset;
  private final int newEndOffset;
  private final Point startPoint;
  private final Point oldEndPoint;
  private final Point newEndPoint;

  public InputEdit(int startOffset, int oldEndOffset, int newEndOffset, Point startPoint, Point oldEndPoint, Point newEndPoint) {
    this.startOffset = startOffset;
    this.oldEndOffset = oldEndOffset;
    this.newEndOffset = newEndOffset;
    this.startPoint = startPoint;
    this.oldEndPoint = oldEndPoint;
    this.newEndPoint = newEndPoint;
  }
}
