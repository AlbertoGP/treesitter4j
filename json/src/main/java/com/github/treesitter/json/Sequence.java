package com.github.treesitter.json;

import java.util.List;

public class Sequence<T> extends JsonRule<T> {
    private List<JsonRule<T>> members;

    public List<JsonRule<T>> getMembers() {
        return members;
    }

    public void setMembers(List<JsonRule<T>> members) {
        this.members = members;
    }
}
