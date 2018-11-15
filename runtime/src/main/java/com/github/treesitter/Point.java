package com.github.treesitter;

public class Point implements Comparable<Point> {
  public static final Point NOWHERE = new Point(0, 1);
  private final int row;
  private final int column;

  public Point(int row, int column) {
    super();
    this.row = row;
    this.column = column;
  }

  public int column() {
    return column;
  }

  public int row() {
    return row;
  }

  public Point add(Point other) {
    if (other.row > 0) return new Point(this.row + other.row, other.column);
    return new Point(this.row, this.column + other.column);
  }

  public Point sub(Point other) {
    if (this.row > other.row) return new Point(this.row - other.row, this.column);
    return new Point(0, this.column - other.column);
  }

  public int compareTo(Point other) {
    int diff = Integer.compare(this.row, other.row);
    if (diff == 0) diff = Integer.compare(this.column, other.column);
    return diff;
  }

  public Point next(boolean isEndOfLine) {
    return isEndOfLine ? new Point(row + 1, 0) : new Point(row, column + 1);
  }
}
