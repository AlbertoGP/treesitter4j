package com.github.treesitter.json;

public class PropertySelectorStep<T> {
    private boolean immediate;
    private int index = -1;
    private boolean named;
    private String text;
    private String type;

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public boolean isNamed() {
        return named;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNamed(boolean named) {
        this.named = named;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }
}
