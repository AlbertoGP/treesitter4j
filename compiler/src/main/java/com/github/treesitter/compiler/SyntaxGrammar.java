package com.github.treesitter.compiler;

import java.util.List;
import java.util.Set;

class SyntaxGrammar {
  List<SyntaxVariable> variables;
  Set<Symbol> extra_tokens;
  Set<Set<Symbol>> expected_conflicts;
  List<ExternalToken> external_tokens;
  Set<Symbol> variables_to_inline;
  Symbol word_token;
}

