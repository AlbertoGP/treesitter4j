package com.github.treesitter;

import java.util.List;
import java.util.function.IntFunction;

import org.flabbergast.treesitter4j.Language.Metadata;

public class Language<T  extends Symbol> {
	public static class Metadata {
		public boolean isNamed();

		public boolean isVisible();
	}

	private interface ParseAction {

	}

	private class TableEntry {
		private final List<ParseAction> actions;
		private final boolean isReusable;
	}

    public IntFunction<T> aliasSequence(int id) {
    }

	public int emptyParseState() {
	}

	public int symbolCount();

	public T name(String symbol);

	public SymbolType symbol(T symbol);

	public int version();version;

	uint32_t symbol_count;
	uint32_t alias_count;
	uint32_t token_count;
	uint32_t external_token_count;const char**symbol_names;const TSSymbolMetadata*symbol_metadata;const uint16_t*parse_table;const TSParseActionEntry*parse_actions;const TSLexMode*lex_modes;const TSSymbol*alias_sequences;
	uint16_t max_alias_sequence_length;

	bool (*lex_fn)(TSLexer *, TSStateId);
  bool (*keyword_lex_fn)(TSLexer *, TSStateId);
  TSSymbol keyword_capture_token;

	public Metadata metadata(T alias) {
		// TODO Auto-generated method stub
		return null;
	}
}
