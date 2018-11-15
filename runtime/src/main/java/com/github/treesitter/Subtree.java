package com.github.treesitter;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;

import static com.github.treesitter.Stack.ERROR_COST_PER_SKIPPED_TREE;

final class Subtree<T extends Symbol> implements  Iterable<Subtree<T>>{

    private static class Child<T extends Symbol> {
  private int aliasSequenceId;
  private final List<Subtree<T>> children;
  private int dynamic_precedence;
  private int firstLeafParseState;
  private Either<T, SpecialSymbol> firstLeafSymbol;
  private int named_child_count;
  private int node_count;
  private int repeat_depth;
  private int visible_child_count;
    public Child(List<Subtree<T>> children) {
        this.children = children;
        this.node_count = 1;
    }

    public int aliasSequenceId() {
        return aliasSequenceId;
    }

    public int childCount() {
return children.size();
    }

    public  int dynamicPrecedence() {
        return  dynamic_precedence;
    }

    public Either<T, SpecialSymbol> firstSymbol() {
return children.get(0).symbol();
    }

    public  int namedChildCount() {
        return  named_child_count;
    }

        public int repeatDepth() {return repeat_depth;}

    public  int visibleChildCount() {
        return visible_child_count;
    }
}

  public static <T extends  Symbol> Subtree<T> error(
            int lookahead_char, Length padding, Length size,
            int bytes_scanned, int parse_state
    ) {
        var result = new Subtree<T>(Either.second(SpecialSymbol.ERROR),
                 null,
                 padding, size, bytes_scanned,
                parse_state, false, false
        );
        result.fragile_left = true;
        result.fragile_right = true;
        result.lookahead_char = lookahead_char;
        return result;
    }

    public static <T extends  Symbol> Subtree<T> errorNode(List<Subtree<T>> children,
                                      boolean extra, Language<T> language) {
        final var result = new Subtree<T>(Either.second(SpecialSymbol.ERROR), null, Length.ZERO,Length.ZERO, 0, 0, false, false);
        result.extra = extra;
        result.setChildren(children, language);
        return result;
    }

    public static <T extends  Symbol>    Subtree <T>missingLeaf(T symbol, Length padding) {
        var result = new Subtree<T>(
                 Either.first(symbol), null, padding, Length.ZERO, 0,
                0, false, false);

            result.is_missing = true;
        return result;
    }
    private final int addtionalOffsetScanned;
  private int bytes_scanned;
  private int error_cost;
  private boolean extra;
  protected boolean fragile_left;
  protected boolean fragile_right;
  private boolean has_changes;
  private boolean has_external_tokens;
  private boolean is_keyword;
  private boolean is_missing;
  private int lookahead_char;
    private boolean named;
    private Length padding;
  private int parse_state;
  private Length size;
private Either<Child<T>, ExternalScanner<T>> state;
private Either<T, SpecialSymbol> symbol;
public Subtree(Either<T, SpecialSymbol >symbol, Either<Child<T>, ExternalScanner<T>>state,  Length padding, Length size, int bytes_scanned, int parse_state, boolean has_external_tokens, boolean is_keyword) {

    this.symbol = symbol;
    this.state = state;
    this.padding = padding;
    this.size = size;
    this.bytes_scanned = bytes_scanned;
    this.parse_state = parse_state;
    this.has_external_tokens = has_external_tokens;
    this.is_keyword = is_keyword;
    this.extra = symbol.test(false, SpecialSymbol.END::equals);
    this.addtionalOffsetScanned = bytes_scanned - size.offset() - padding.offset();
}

int aliasSequenceId() {
    return state == null ? 0 : state.applyOrRight(Child::aliasSequenceId, 0);
}

    public int childCount() {
        return state == null ? 0 : state.applyOrRight(Child::childCount, 0);
    }
    private static class Edit{
        Length start;
        Length old_end;
        Length new_end;

        public Edit(InputEdit edit) {
            start = new Length(edit.startOffset(), edit.startPoint());
            old_end= new Length(edit.oldEndOffset(), edit.oldEndPoint());
            new_end = new Length(edit.newEndOffset(), edit.newEndPoint());
        }

        public Edit(Length start, Length old_end, Length new_end) {
            this.start = start;
            this.old_end = old_end;
            this.new_end = new_end;
        }
    } ;

    public int dynamicPrecedence() { return state == null ? 0 : state.applyOrRight(Child::dynamicPrecedence, 0); }
private  class StackEntry      {
    final Subtree<T> tree;
    final Edit edit;

    public StackEntry(Subtree<T> tree, Edit edit) {
        this.tree = tree;
        this.edit = edit;
    }
};
    public Subtree<T> edit(InputEdit inputEdit) {

        Deque<StackEntry> stack = new ArrayDeque<>();
        stack.push(new StackEntry(this, new Edit(inputEdit)));
StackEntry entry ;
  while ((entry = stack.poll()) != null) {
      final var edit = entry.edit;
    var is_noop = edit.old_end.offset() == edit.start.offset() && edit.new_end.offset() == edit.start.offset();
    var is_pure_insertion = edit.old_end.offset() == edit.start.offset();
    var bytes_scanned = entry.tree.bytes_scanned;
    if (is_noop && edit.start.offset() >= bytes_scanned) continue;

    var size = entry.tree.size;
    var padding = entry .tree .padding;

    // If the edit is entirely within the space before this subtree, then shift this
    // subtree over according to the edit without changing its size.
    if (edit.old_end.offset() <= padding.offset()) {
      padding = edit.new_end.add(padding.sub(edit.old_end));
    }

    // If the edit starts in the space before this subtree and extends into this subtree,
    // shrink the subtree's content to compensate for the change in the space before it.
    else if (edit.start.offset() < padding.offset()) {
      size = size.sub(edit.old_end.sub( padding));
      padding = edit.new_end;
    }

    // If the edit is a pure insertion right at the start of the subtree,
    // shift the subtree over according to the insertion.
    else if (edit.start.offset() == padding.offset() && is_pure_insertion) {
      padding = edit.new_end;
    }

    // If the edit is within this subtree, resize the subtree to reflect the edit.
    else {
     var total_bytes = padding.offset() + size.offset();
      if (edit.start.offset() < total_bytes ||
         (edit.start.offset() == total_bytes && is_pure_insertion)) {
        size =
               edit.new_end.sub( padding).add(

          size.sub(edit.old_end.sub(padding))
        );
      }
    }

    entry.tree.has_changes = true;

    var child_left= Length.ZERO;var child_right = Length.ZERO;
    boolean first = true;
    for (var child : entry.tree) {
      final var child_size = child.size();
      child_left = child_right;
      child_right = child_left.add(child_size);

      // If this child starts after the edit, then we're done processing children.
      if (child_left.offset() > edit.old_end.offset() ||
          (child_left.offset() == edit.old_end.offset() && child_size.offset() > 0 && !first)) break;
      first = false;

      // Transform edit into the child's coordinate space.
      Edit child_edit = new Edit(
        edit.start.sub(child_left),
        edit.old_end.sub(child_left),
        edit.new_end.sub(child_left)
      );

      // Clamp child_edit to the child's bounds.
      if (edit.start.offset() < child_left.offset()) child_edit.start = Length.ZERO;
      if (edit.old_end.offset() < child_left.offset()) child_edit.old_end = Length.ZERO;
      if (edit.new_end.offset() < child_left.offset()) child_edit.new_end = Length.ZERO;
      if (edit.old_end.offset() > child_right.offset()) child_edit.old_end = child_size;

      // Interpret all inserted text as applying to the *first* child that touches the edit.
      // Subsequent children are only never have any text inserted into them; they are only
      // shrunk to compensate for the edit.
      if (child_right.offset() > edit.start.offset() ||
          (child_right.offset() == edit.start.offset() && is_pure_insertion)) {
        edit.new_end = edit.start;
      }

      // Children that occur before the edit are not reshaped by the edit.
      else {
        child_edit.old_end = child_edit.start;
        child_edit.new_end = child_edit.start;
      }

      // Queue processing of this child's subtree.
      stack.push(new StackEntry(child, child_edit));
    }
  }

  return this;
  }

  public int errorCost() {
    return is_missing ? Stack.ERROR_COST_PER_MISSING_TREE + Stack.ERROR_COST_PER_RECOVERY : error_cost;
  }

    public boolean externalScannerStateEqual(Subtree<T> subtree) {
      return state != null && state.test(false, es -> subtree.state ==null ? false : subtree.state.applyOrLeft(false, es::equals));
    }

    public boolean extra() { return extra;}

  public boolean hasChanges() {
    return has_changes;
  }

boolean hasExternalTokens() {
  return has_external_tokens;
}

    public final boolean isError() { return symbol().test(false, SpecialSymbol.ERROR::equals); }

boolean isFragile() {
  return fragile_left || fragile_right;
}

boolean isFragileLeft() {
  return fragile_left;
}

boolean isFragileRight() {
  return fragile_right;
}

public boolean isNamed() { return named;}

    @Override
    public Iterator<Subtree<T>> iterator() {
        return state == null
                 ? Collections.emptyIterator() : state.applyOrRight(c -> c.children.iterator(), Collections.emptyIterator());
    }

    public int leafParseState() { return parse_state; }

  public Either<T, SpecialSymbol> leafSymbol() { return state == null ? symbol : state.apply(Child::firstSymbol, s -> Either.first(s.state()));}

  public boolean missing() {
return is_missing;
  }

  public boolean named() {
    return named;
  }

public int namedChildCount() {

    return state == null ? 0 : state.applyOrRight(Child::namedChildCount ,  0);
}

public int nodeCount() {
  return state == null?  1 : state.applyOrRight(Child::childCount, 1);
}

public final Length padding() { return padding; }

    private int repeatDepth() {
        return state == null ? 0 : state.applyOrRight(Child::repeatDepth, 0);
    }

    void setChildren(
           List<Subtree<T>>children, Language <T>language) {

        var childState = new Child<T>(children);
        state = Either.first(childState);
        error_cost = 0;
        has_external_tokens = false;
        var non_extra_index = 0;
   var alias_sequence = language.aliasSequence(aliasSequenceId());

   boolean first = true;
        for (var child : children) {

            if (first) {
                first = false;
                padding = child.padding();
                size = child.size();
                bytes_scanned = child.bytes_scanned;
            } else {
                var bytes_scanned = padding.offset() + size.offset() + child.bytes_scanned;
                if (bytes_scanned > this.bytes_scanned) this.bytes_scanned = bytes_scanned;
                size = size.add(child.totalSize());
            }

            if (!child.symbol().applyOrLeft(false, SpecialSymbol.ERROR_REPEATED::equals)) {
                error_cost += child.errorCost();
            }

            childState.dynamic_precedence += child.dynamicPrecedence();
            childState.node_count += child.nodeCount();
final var alias = alias_sequence.apply(non_extra_index);
            if (alias != null && !child.extra()) {
                childState.visible_child_count++;
                if (language.metadata(alias).isNamed()) {
                    childState.named_child_count++;
                }
            } else if (child.symbol().applyOrRight(s -> language.metadata(s).isVisible(), false)) {
                childState.visible_child_count++;
                if (child.isNamed())childState.named_child_count++;
            } else if (child.childCount() > 0) {
                childState.visible_child_count += child.visibleChildCount();
                childState.named_child_count += child.namedChildCount();
            }

            if (child.hasExternalTokens()) has_external_tokens = true;

            if (child.isError()) {
                fragile_left = fragile_right = true;
                parse_state = language.emptyParseState();
            }

            if (!child.extra()) non_extra_index++;
        }

        if (symbol.applyOrLeft(false, SpecialSymbol::isError)) {
            error_cost +=
                    Stack.ERROR_COST_PER_RECOVERY +
                            Stack.ERROR_COST_PER_SKIPPED_CHAR * size.offset() +
                    Stack.ERROR_COST_PER_SKIPPED_LINE * size.point().row();
            error_cost += children.stream().filter(c -> !c.extra() || !(c.isError() && childState.childCount() ==0 )).mapToInt(c ->{

             if (c.symbol().applyOrRight(s -> language.metadata(s).isVisible(), false)) return ERROR_COST_PER_SKIPPED_TREE ;
             if (c.childCount() > 0) return  ERROR_COST_PER_SKIPPED_TREE * c.visibleChildCount();
             return 0;
            }).sum();
        }

        if (!children.isEmpty()) {
            final var first_child = children.get(0);
            final var last_child = children.get(children.size()-1);

            if (first_child.isFragile()) fragile_left = true;
            if (last_child.isFragile()) fragile_right = true;
            final var visible = symbol().applyOrRight(s -> language.metadata(s).isVisible(), false);

            if (
                    children.size() == 2 &&
                    !visible && !named &&
                    first_child.symbol.equals(symbol) &&
                    last_child.symbol.equals(symbol) ) {
                if (first_child.repeatDepth() > last_child.repeatDepth()) {
                    childState.repeat_depth = first_child.repeatDepth() + 1;
                } else {
                    childState.repeat_depth = last_child.repeatDepth() + 1;
                }
            }
        }
    }

public final void setExtra(){ extra = true; }

public final Length size() { return size; }

    public String string(Language<T> language, boolean includeAll) {
    var buffer = new StringBuilder();
    string(buffer, language, true, includeAll, null, false);
    return
            buffer.toString();
    }
  public void string(StringBuilder buffer, Language<T> language, boolean isRoot, boolean includeAll,T alias_symbol, boolean alias_is_named) {


  final var visible =
    includeAll ||
    isRoot ||
    is_missing ||
    (symbol().applyOrRight(s -> language.metadata(s).isVisible(), false) && isNamed()) ||
    alias_is_named;

  if (visible && !isRoot) {
      buffer.append(" ");
  }

  if (visible) {
    if (isError() && childCount() == 0 && size().offset()> 0) {
        buffer.append("(UNEXPECTED ").appendCodePoint(lookahead_char);
    } else if (is_missing) {
      buffer.append("(MISSING");
    } else {
      buffer.append("(").append((alias_symbol == null? symbol().apply(Symbol::name, SpecialSymbol::name):alias_symbol.name()));
    }
  }

  if (childCount()> 0) {
    var alias_sequence = language.aliasSequence(aliasSequenceId());
    var structural_child_index = 0;
    for (var child : this) {
      if (child.extra()) {
          child.string(
                  buffer, language, false, includeAll, null, false);
      } else {
        var child_alias_symbol = alias_sequence == null ? alias_sequence.apply(structural_child_index) : null;
        child.string(
          buffer,
          language, false, includeAll,
          child_alias_symbol,
          child_alias_symbol != null ? language.metadata(child_alias_symbol).isNamed(): false
        );
        structural_child_index++;
      }
    }
  }

  if (visible) buffer.append(")");
  }

public final Either<T, SpecialSymbol> symbol() { return symbol;}

public final Length totalSize() { return padding().add(size()); }

public int visibleChildCount() {

    return state == null ? 0 : state.applyOrRight(Child::visibleChildCount ,  0);
}
}
