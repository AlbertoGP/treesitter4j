package com.github.treesitter;

import java.util.function.Function;

public abstract class Either<A, B> {
  public <A, B> Either<A, B> first(A value) {
    return new FirstEither<A, B>(value);
  }

  public <A, B> Either<A, B> second(B value) {
    return new SecondEither<A, B>(value);
  }

  public abstract <R> R apply(
      Function<? super A, ? extends R> firstHandler,
      Function<? super B, ? extends R> secondHandler);

  public abstract boolean contains(Object o);

  private static class FirstEither<A, B> extends Either<A, B> {
    private final A value;

    public FirstEither(A value) {
      this.value = value;
    }

    @Override
    public int hashCode() {
      return 31 * value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof FirstEither) && value.equals(((FirstEither) obj).value);
    }

    @Override
    public <R> R apply(
        Function<? super A, ? extends R> firstHandler,
        Function<? super B, ? extends R> secondHandler) {
      return firstHandler.apply(value);
    }

    @Override
    public boolean contains(Object o) {
      return value.equals(o);
    }
  }

  private static class SecondEither<A, B> extends Either<A, B> {
    private final B value;

    public SecondEither(B value) {
      this.value = value;
    }

    @Override
    public int hashCode() {
      return 7 * value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof SecondEither) && value.equals(((SecondEither) obj).value);
    }

    @Override
    public <R> R apply(
        Function<? super A, ? extends R> firstHandler,
        Function<? super B, ? extends R> secondHandler) {
      return secondHandler.apply(value);
    }

    @Override
    public boolean contains(Object o) {
      return value.equals(o);
    }
  }
}
