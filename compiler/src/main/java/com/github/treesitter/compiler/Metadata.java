package com.github.treesitter.compiler;

import java.util.Objects;
import java.util.function.Consumer;

class Metadata extends Rule {
  static Metadata activePrec(int precedence, Rule rule) {
  return addMetadata(rule, params -> {
    params.has_precedence = true;
    params.precedence = precedence;
    params.is_active = true;
  });
}

private static Metadata addMetadata(Rule rule, Consumer<MetadataParams> callback) {
  if (rule instanceof Metadata) {
    Metadata metadata = (Metadata)rule;
    callback.accept(metadata.params);
    return metadata;
  } else {
    MetadataParams params = new MetadataParams();
    callback.accept(params);
    return new Metadata(rule, params);
  }
}

static Metadata alias(String value, boolean is_named, Rule rule) {
  return addMetadata(rule, params -> {
    params.alias.value = value;
    params.alias.is_named = is_named;
  });
}

static Metadata immediateToken(Rule rule) {
  return addMetadata(rule, params -> {
    params.is_token = true;
    params.is_main_token = true;
  });
}

static Metadata mainToken(Rule rule) {
  return addMetadata(rule, params -> {
    if (!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = 0;
    }
    params.is_main_token = true;
  });
}

static Metadata merge(Rule rule, MetadataParams new_params) {
  return addMetadata(rule, params ->  {
    if (new_params.has_precedence   &&!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = new_params.precedence;
    }

    if (new_params.has_associativity &&  !params.has_associativity) {
      params.has_associativity = true;
      params.associativity = new_params.associativity;
    }

    if (new_params.dynamic_precedence != 0) {
      params.dynamic_precedence = new_params.dynamic_precedence;
    }

    if (new_params.is_string) params.is_string = true;
    if (new_params.is_active) params.is_active = true;
    if (new_params.is_main_token) params.is_main_token = true;

    if (!new_params.alias.value.isEmpty()) {
      params.alias = new_params.alias;
    }
  });
}

static Metadata prec(int precedence, Rule rule) {
  return addMetadata(rule, params -> {
    if (!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = precedence;
    }
  });
}

static Metadata precDynamic(int dynamic_precedence, Rule rule) {
  return addMetadata(rule, params -> params.dynamic_precedence = dynamic_precedence);
}

static Metadata precLeft(int precedence, Rule rule) {
  return addMetadata(rule, params -> {
    if (!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = precedence;
    }
    if (!params.has_associativity) {
      params.has_associativity = true;
      params.associativity = Associativity.LEFT;
    }
  });
}

static Metadata precRight(int precedence, Rule rule) {
  return addMetadata(rule, params -> {
    if (!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = precedence;
    }
    if (!params.has_associativity) {
      params.has_associativity = true;
      params.associativity = Associativity.RIGHT;
    }
  });
}

static Metadata separator(Rule rule) {
  return addMetadata(rule, params -> {
    if (!params.has_precedence) {
      params.has_precedence = true;
      params.precedence = Integer.MIN_VALUE;
    }
    params.is_active = true;
  });
}

static Metadata token(Rule rule) {
  return addMetadata(rule, params -> params.is_token = true);
}
  MetadataParams params;
  Rule rule;
  Metadata(Rule rule, MetadataParams params) { this.rule = rule; this.params = params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Metadata metadata = (Metadata) o;
    return params.equals(metadata.params) &&
            rule.equals(metadata.rule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, rule);
  }

}
