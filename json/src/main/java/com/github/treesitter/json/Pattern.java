package com.github.treesitter.json;

public class Pattern<T> extends JsonRule<T> {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}