package com.github.treesitter;

import java.util.function.Predicate;

public interface ExternalScanner<T extends Symbol> {
    boolean scan(Lexer<T> lexer, Predicate<T> whitelist);
    ExternalScanner<T> fork();
    T state();
}
