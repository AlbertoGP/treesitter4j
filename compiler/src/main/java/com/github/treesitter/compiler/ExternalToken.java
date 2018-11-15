package com.github.treesitter.compiler;

class ExternalToken {
  String name;
  VariableType type;
  rules::Symbol corresponding_internal_token;

  bool operator==(const ExternalToken &) const;
bool ExternalToken::operator==(const ExternalToken &other) const {
  return name == other.name &&
    type == other.type &&
    corresponding_internal_token == other.corresponding_internal_token;
}
};

