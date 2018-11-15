package com.github.treesitter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Lexer<T extends Symbol> {
  private static final Range DEFAULT_RANGE =
      Range.of(
          new Point(0, 0), new Point(Integer.MAX_VALUE, Integer.MAX_VALUE), 0, Integer.MAX_VALUE);
  private Length currentPosition = new Length(Integer.MAX_VALUE, new Point(0, 0));
  private Length tokenStartPosition;
  private Length tokenEndPosition;
  private List<Range> includedRanges;
  private int currentIncludedRange;
  private final Logger logger;
  private final CharSequence input;
  private int lookahead;
  private boolean lookaheadIsPair;
  private T resultSymbol;

  private void fillLookahead() {
    if (currentPosition.offset() == input.length() - 1) {
      lookahead = 0;
      return;
    }
    if (Character.isSurrogate(input.charAt(currentPosition.offset()))) {
      if (currentPosition.offset() == input.length() - 2) {
        throw new IllegalArgumentException("Unmatched surrogate pair at end of input.");
      }
      lookahead =
          Character.toCodePoint(
              input.charAt(currentPosition.offset()), input.charAt(currentPosition.offset() + 1));
      lookaheadIsPair = true;
    } else {
      lookahead = input.charAt(currentPosition.offset());
      lookaheadIsPair = false;
    }
  }

  public void advance(boolean skip) {
    if (lookahead != 0) {
      currentPosition = currentPosition.advance(lookaheadIsPair ? 2 : 1, lookahead == '\n');
    }
    if (currentPosition.offset() == includedRanges.get(currentIncludedRange).endOffset()) {
      if (currentIncludedRange == includedRanges.size()) {
        lookahead = 0;
        return;
      } else {
        currentIncludedRange++;
        currentPosition = includedRanges.get(currentIncludedRange).startLength();
      }
    }
    if (skip) {
      tokenStartPosition = currentPosition;
      logger.logLex("skip", lookahead);
    } else {
      logger.logLex("consume", lookahead);
    }
    fillLookahead();
  }

  public void markEnd() {
    if (currentIncludedRange > 0
        && currentPosition.offset() == includedRanges.get(currentIncludedRange).startOffset()) {
      tokenEndPosition = includedRanges.get(currentIncludedRange - 1).endLength();
    } else {
      tokenEndPosition = currentPosition;
    }
  }

  public int column() {
    int goalOffset = currentPosition.offset();
    // TODO: I think this is a bug; I think it will fail to deal correctly with
    // multibyte chars
    currentPosition =
        new Length(
            currentPosition.offset() - currentPosition.point().column(),
            new Point(currentPosition.point().row(), 0));
    int result = 0;
    while (currentPosition.offset() < goalOffset) {
      advance(false);
    }
    return result;
  }

  public boolean isAtIncludedRangeStart() {
    return currentPosition.offset() == includedRanges.get(currentIncludedRange).startOffset();
  }

  private Lexer(Logger logger, CharSequence input, Range... ranges) {
    super();
    this.logger = logger;
    this.input = input;
    if (ranges.length == 0) {
      includedRanges = Collections.singletonList(DEFAULT_RANGE);
    } else {
      includedRanges = Arrays.asList(ranges);
    }
    reset(Length.ZERO);
  }

  public void setIncludedRanges(Stream<Range> ranges) {
    includedRanges = ranges.collect(Collectors.toList());
    if (includedRanges.isEmpty()) {
      includedRanges = Collections.singletonList(DEFAULT_RANGE);
    }
    goTo(currentPosition);
  }

  private void goTo(Length position) {
    boolean foundIncludedRange = false;
    for (int i = 0; i < includedRanges.size(); i++) {
      Range includedRange = includedRanges.get(i);
      if (includedRange.endOffset() > position.offset()) {
        if (includedRange.startOffset() > position.offset()) {
          position = includedRange.startLength();
        }

        currentIncludedRange = i;
        foundIncludedRange = true;
        break;
      }
    }

    if (!foundIncludedRange) {
      position = includedRanges.get(includedRanges.size() - 1).endLength();
    }

    tokenStartPosition = position;
    tokenEndPosition = Length.UNDEFINED;
    currentPosition = position;
    lookahead = 0;
  }

  public void reset(Length position) {
    if (currentPosition.offset() != position.offset()) {
      goTo(position);
    }
  }
}
