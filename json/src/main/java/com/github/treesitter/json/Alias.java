package com.github.treesitter.json;

public class Alias<T>extends JsonRule<T> {
    private JsonRule<T>content;
    private boolean named;
    private String value;

    public JsonRule<T> getContent() {
        return content;
    }

    public String getValue() {
        return value;
    }

    public boolean isNamed() {
        return named;
    }

    public void setContent(JsonRule<T> content) {
        this.content = content;
    }

    public void setNamed(boolean named) {
        this.named = named;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
