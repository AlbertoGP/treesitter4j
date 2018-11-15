package com.github.treesitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Either<A, B> {
  private static class FirstEither<A, B> extends Either<A, B> {
    private final A value;

    public FirstEither(A value) {
      this.value = value;
    }

    @Override
    public <R> R apply(
        Function<? super A, ? extends R> firstHandler,
        Function<? super B, ? extends R> secondHandler) {
      return firstHandler.apply(value);
    }

    @Override
    public <R> R applyOrRight(Function<? super A, ? extends R> firstHandler, R secondHandler) {
      return firstHandler.apply(value);
    }

    @Override
    public <R> R applyOrLeft(R firstHandler, Function<? super B, ? extends R> secondHandler) {
      return firstHandler;
    }

    @Override
    public boolean contains(Object o) {
      return value.equals(o);
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof FirstEither) && value.equals(((FirstEither) obj).value);
    }

    @Override
    public int hashCode() {
      return 31 * value.hashCode();
    }

    @Override
    public <X, Y> Either<X, Y> map(
        Function<? super A, X> firstHandler, Function<? super B, Y> secondHandler) {
      return new FirstEither<>(firstHandler.apply(value));
    }

    @Override
    public B orLeft(B alternate) {
      return alternate;
    }

    @Override
    public A orRight(A alternate) {
      return value;
    }

      @Override
      public boolean test(Predicate<? super A> testA, Predicate<? super B> testB) {
          return testA.test(value);
      }

      @Override
      public boolean test(Predicate<? super A> testA, boolean resultB) {
          return testA.test(value);
      }

      @Override
      public boolean test(boolean resultA, Predicate<? super B> testB) {
          return resultA;
      }
  }

  private static class SecondEither<A, B> extends Either<A, B> {
    private final B value;

    public SecondEither(B value) {
      this.value = value;
    }

    @Override
    public <R> R apply(
        Function<? super A, ? extends R> firstHandler,
        Function<? super B, ? extends R> secondHandler) {
      return secondHandler.apply(value);
    }

    @Override
    public <R> R applyOrRight(Function<? super A, ? extends R> firstHandler, R secondHandler) {
      return secondHandler;
    }

    @Override
    public <R> R applyOrLeft(R firstHandler, Function<? super B, ? extends R> secondHandler) {
      return secondHandler.apply(value);
    }

    @Override
    public boolean contains(Object o) {
      return value.equals(o);
    }

    @Override
    public boolean equals(Object obj) {
      return (obj instanceof SecondEither) && value.equals(((SecondEither) obj).value);
    }

    @Override
    public int hashCode() {
      return 7 * value.hashCode();
    }

    @Override
    public <X, Y> Either<X, Y> map(
        Function<? super A, X> firstHandler, Function<? super B, Y> secondHandler) {
      return new SecondEither<>(secondHandler.apply(value));
    }

    @Override
    public B orLeft(B alternate) {
      return value;
    }

    @Override
    public A orRight(A alternate) {
      return alternate;
    }

      @Override
      public boolean test(Predicate<? super A> testA, Predicate<? super B> testB) {
          return testB.test(value);
      }

      @Override
      public boolean test(Predicate<? super A> testA, boolean resultB) {
          return resultB;
      }

      @Override
      public boolean test(boolean resultA, Predicate<? super B> testB) {
          return testB.test(value);
      }
  }

  public static <A, B> Either<A, B> first(A value) {
    return new FirstEither<A, B>(value);
  }

  public static <A, B> Either<A, B> second(B value) {
    return new SecondEither<A, B>(value);
  }
  public static <A, B, X, Y> Function<Either<A, B>, Either<X, Y>> transform(Function<? super A, X> leftMapper, Function<? super B, Y>rightMapper) {
      return e -> e.map(leftMapper, rightMapper);
    }

    public static <A, B, R> Consumer<Either<A, B>> consume(Function<?super A, ? extends R> firstFunction, Function<?super B, ? extends R> secondFunction, Consumer<? super R> consumer) {
      return e-> consumer.accept(e.apply(firstFunction, secondFunction));
    }

  public static <A, B, R> Function<Either<A, B>, R> applier(
      Function<? super A, ? extends R> firstHandler,
                                                           Function<? super B, ? extends R> secondHandler) { return e -> e.apply(firstHandler, secondHandler);}
  public static <A, B, R> Predicate<Either<A, B>> tester(
      Predicate<? super A> firstTester,
      Predicate<? super B> secondTester) { return e -> e.test(firstTester, secondTester);}

  public abstract <R> R apply(
      Function<? super A, ? extends R> firstHandler,
      Function<? super B, ? extends R> secondHandler);


  public abstract <R> R applyOrRight(
      Function<? super A, ? extends R> firstHandler,
      R secondHandler);
  public abstract <R> R applyOrLeft(R firstHandler,
      Function<? super B, ? extends R> secondHandler);

  public abstract boolean contains(Object o);

  public abstract <X, Y> Either<X, Y> map(
      Function<? super A, X> firstHandler, Function<? super B, Y> secondHandler);

  public abstract B orLeft(B alternate);

  public abstract A orRight(A alternate);

    public abstract boolean test (Predicate<?super A> testA, Predicate<? super B> testB);
  public abstract boolean test (Predicate<?super A> testA, boolean resultB);
  public abstract boolean test (boolean resultA, Predicate<? super B> testB);
}
