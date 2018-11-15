package com.github.treesitter.json;

public class Symbol<T> extends JsonRule<T> {
    private T name;

    public T getName() {
        return name;
    }

    public void setName(T name) {
        this.name = name;
    }
}
