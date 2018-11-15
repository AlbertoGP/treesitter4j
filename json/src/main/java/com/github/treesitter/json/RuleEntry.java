package com.github.treesitter.json;

import java.util.List;
import java.util.Map;

public class RuleEntry<T> {
   private Map<String, String> properties;
   private List<List<PropertySelectorStep<T>>> selectors;

   public Map<String, String> getProperties() {
      return properties;
   }

   public List<List<PropertySelectorStep<T>>> getSelectors() {
      return selectors;
   }

   public void setProperties(Map<String, String> properties) {
      this.properties = properties;
   }

   public void setSelectors(List<List<PropertySelectorStep<T>>> selectors) {
      this.selectors = selectors;
   }
}
