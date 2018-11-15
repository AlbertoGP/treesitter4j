package com.github.treesitter;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Builder<T extends Symbol> {
    public enum Prec {LEFT, RIGHT, DYNAMIC}

    public class NamedRule {
private final String name;
private Rule<T> rule;
        public NamedRule(String name) {
            this.name = name;
        }

public String name() { return name;}

        public void set(Rule<T> rule) {
    this.rule = rule;
        }
    }

    public static class Rule<T extends Symbol> {
        public Rule<T> choice(Rule<T>...choices) {

        }
        public Rule<T> error(String content) {

        }

    public Rule<T> immediateToken(Rule<T> rule) {
    }

public Rule<T> optional(Rule<T> rule) {
            return choice(rule, blank());
}

        public Rule<T> prec(int number, Prec prec, Rule<T> rule) {

        }

public Rule<T> repeat(Rule<T> rule) {



}public Rule<T> repeat1(Rule<T> rule) {

}       public Rule<T> then(Pattern pattern) {

        }

        public Rule<T> then(String value) {

        }

        public Rule<T> then(T symbol) {

        }

        public Rule<T> then(Builder<T>.NamedRule rule) {

        }

    public Rule<T> token(Rule<T> rule) {
    }
    }

    public static <T extends Symbol> Rule<T>blank() {

    }

    public static <T extends Symbol> Rule<T> start() {

    }
    private final Supplier<? extends ExternalScanner<T>> scannerConstructor;

    public Builder(Supplier<? extends ExternalScanner<T>> scannerConstructor) {
        this.scannerConstructor = scannerConstructor;
    }


    public Language<T> build() {

    }

    public NamedRule create(String name) {
        return new NamedRule(name);
    }
}
