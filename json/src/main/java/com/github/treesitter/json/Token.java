package com.github.treesitter.json;

public class Token<T> extends JsonRule<T> {
    private JsonRule<T> content;

    public JsonRule<T> getContent() {
        return content;
    }

    public void setContent(JsonRule<T> content) {
        this.content = content;
    }
}
