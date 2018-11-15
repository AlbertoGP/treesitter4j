package com.github.treesitter;

public class Length {
  public static final Length UNDEFINED = new Length(0, Point.NOWHERE);
  public static final Length ZERO = new Length(0, new Point(0, 0));
  private final int offset;
  private final Point point;

  public Length(int offset, Point point) {
    this.offset = offset;
    this.point = point;
  }

  public int offset() {
    return offset;
  }

  public Point point() {
    return point;
  }

  public boolean isUndefined() {
    return offset == 0 && point.column() != 0;
  }

  public Length min(Length other) {
    return this.offset < other.offset ? this : other;
  }

  public Length add(Length other) {
    return new Length(this.offset + other.offset, this.point.add(other.point));
  }

  public Length sub(Length other) {
    return new Length(this.offset - other.offset, this.point.sub(other.point));
  }

  public Length advance(int offsetIncrement, boolean isEndOfLine) {
    return new Length(offset + offsetIncrement, point.next(isEndOfLine));
  }
}
