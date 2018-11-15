package com.github.treesitter.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonGrammar<T> {
  private List<List<String>> conflicts = Collections.emptyList();
  private List<T> externals;
  private List<JsonRule<T>> extras = Collections.emptyList();
  private List<String> inline = Collections.emptyList();
    private String name;
    private Map<String, RuleEntry<T>> rules;
  private String word;

    public List<List<String>> getConflicts() {
        return conflicts;
    }

    public List<T> getExternals() {
        return externals;
    }

    public List<JsonRule<T>> getExtras() {
        return extras;
    }

    public List<String> getInline() {
        return inline;
    }

    public String getName() {
        return name;
    }

    public Map<String, RuleEntry<T>> getRules() {
        return rules;
    }

    public String getWord() {
        return word;
    }

    public void setConflicts(List<List<String>> conflicts) {
        this.conflicts = conflicts;
    }

    public void setExternals(List<T> externals) {
        this.externals = externals;
    }

    public void setExtras(List<JsonRule<T>> extras) {
        this.extras = extras;
    }

    public void setInline(List<String> inline) {
        this.inline = inline;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRules(Map<String, RuleEntry<T>> rules) {
        this.rules = rules;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
