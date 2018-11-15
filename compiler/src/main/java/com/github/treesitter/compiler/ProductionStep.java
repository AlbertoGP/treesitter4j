package com.github.treesitter.compiler;

import java.util.Objects;

class ProductionStep {
    private final Symbol symbol;
  private final int precedence;
  private final Associativity associativity;
  private final Alias alias;

bool ProductionStep::operator<(const ProductionStep &other) const {
 if (symbol < other.symbol) return true;
 if (other.symbol < symbol) return false;
 if (precedence < other.precedence) return true;
 if (other.precedence < precedence) return false;
 if (associativity < other.associativity) return true;
 if (other.associativity < associativity) return false;
 return alias < other.alias;
}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionStep that = (ProductionStep) o;
        return precedence == that.precedence &&
                symbol.equals(that.symbol) &&
                associativity == that.associativity &&
                alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, precedence, associativity, alias);
    }

    ProductionStep(Symbol symbol, int precedence, Associativity associativity, Alias alias) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.associativity = associativity;
        this.alias = alias;
    }
}
