package com.github.treesitter.json;

public class LeftPrecedence<T> extends JsonRule<T> {
    private JsonRule<T> content;
    private int value;

    public JsonRule<T> getContent() {
        return content;
    }

    public int getValue() {
        return value;
    }

    public void setContent(JsonRule<T> content) {
        this.content = content;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
