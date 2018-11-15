package com.github.treesitter;

public interface Range {
  public static Range of(Point start, Point end, int startOffset, int endOffset) {
    return new Range() {

      public Point startPoint() {
        return start;
      }

      public int startOffset() {
        return startOffset;
      }

      public Point endPoint() {
        return end;
      }

      public int endOffset() {
        return endOffset;
      }
    };
  }

  Point startPoint();

  Point endPoint();

  int startOffset();

  int endOffset();

  default Length startLength() {
    return new Length(startOffset(), startPoint());
  }

  default Length endLength() {
    return new Length(endOffset(), endPoint());
  }
}
