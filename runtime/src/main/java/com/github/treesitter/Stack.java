package com.github.treesitter;

import flabbergast.Ptr;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

class Stack<T extends Symbol> {
private enum Action {
       STOP, POP
}
   private enum Status {ACTIVE, PAUSED, HALTED}
public interface IterateCallback{
    void accept(int state, int subtreeCount);
    }

   private class Entry {
       int dynamicPrecedence;
       int errorCost;
       List<Link> links = new ArrayList<>();
       int nodeCount;
       Length position;
       int state;

public Entry(Entry previousNode, Subtree<T> subtree,
                                 boolean isPending, int state) {
this.state = state;
  if (previousNode != null) {
      links.add(new Link(previousNode, subtree, isPending));

    position = previousNode.position;
    errorCost = previousNode.errorCost;
    dynamicPrecedence = previousNode.dynamicPrecedence;
    nodeCount = previousNode.nodeCount;
    if (subtree != null) {
      errorCost += subtree.errorCost();
      position = position.add(subtree.totalSize());
      nodeCount += subtree.nodeCount();
      dynamicPrecedence += subtree.dynamicPrecedence();
    }
  } else {
    position = Length.ZERO;
  }
}

void addLink(Link link) {
  if (link.entry == this) return;

  for (var i = 0; i < links.size(); i++) {
      var existingLink = links.get(i);
    if (isEquivalent(existingLink.subtree, link.subtree)) {
      // In general, we preserve ambiguities until they are removed from the stack
      // during a pop operation where multiple paths lead to the same entry. But in
      // the special case where two links directly connect the same pair of nodes,
      // we can safely remove the ambiguity ahead of time without changing behavior.
      if (existingLink.entry == link.entry) {
        if (
          link.subtree.dynamicPrecedence() >
          existingLink.subtree.dynamicPrecedence()
        ) {
          existingLink.subtree = link.subtree;
          dynamicPrecedence =
            link.entry.dynamicPrecedence + link.subtree.dynamicPrecedence();
        }
        return;
      }

      // If the previous nodes are mergeable, merge them recursively.
      if (existingLink.entry.state == link.entry.state &&
          existingLink.entry.position.offset() == link.entry.position.offset()) {
          link.entry.links.forEach(existingLink.entry::addLink);
          var dynamicPrecedence = link.entry.dynamicPrecedence;
        if (link.subtree != null) {
          dynamicPrecedence += link.subtree.dynamicPrecedence();
        }
        if (dynamicPrecedence > this.dynamicPrecedence) {
          this.dynamicPrecedence = dynamicPrecedence;
        }
        return;
      }
    }
  }

    var nodeCount = link.entry.nodeCount;
    var dynamicPrecedence = link.entry.dynamicPrecedence;
  links.add( link);

  if (link.subtree != null) {
    nodeCount += link.subtree.nodeCount();
    dynamicPrecedence += link.subtree.dynamicPrecedence();
  }

  if (nodeCount > this.nodeCount) this.nodeCount = nodeCount;
  if (dynamicPrecedence > this.dynamicPrecedence) this.dynamicPrecedence = dynamicPrecedence;
}   }

   private class Head {
       Entry entry;
       Subtree<T> lastExternalToken;
       T lookaheadWhenPaused;
       int nodeCountAtLastError;
       Status status;
       List<Summary> summary;
       public Head (
       Entry entry,
       Subtree<T> lastExternalToken,
       List<Summary> summary,
       int nodeCountAtLastError,
       T lookaheadWhenPaused,
       Status status) {
           this.entry = entry;
           this.lastExternalToken = lastExternalToken;
           this.summary = summary;
           this.nodeCountAtLastError = nodeCountAtLastError;
           this.lookaheadWhenPaused = lookaheadWhenPaused;
           this.status = status;
       }   }

   private class Link {
       Entry entry;
       boolean isPending;
       Subtree<T> subtree;

       public Link(Entry previousNode, Subtree<T> subtree, boolean isPending) {
           this.entry = previousNode;
           this.subtree = subtree;
           this.isPending = isPending;
       }
   }

   private class Slice{
       List<Subtree<T>> subtrees;
       int version;

       public Slice(List<Subtree<T>> subtrees, int version) {
           this.subtrees = subtrees;
           this.version = version;
       }
   }

   private class StackIterator {
       public Entry entry;
        public boolean isPending;
       public int subtreeCount;
       public List<Subtree<T> > subtrees = new ArrayList<>();

       public StackIterator(Entry entry, boolean isPending) {
           this.entry = entry;
           this.isPending = isPending;
       }
   }

   private class Summary {
       final int depth;
       final Length position;
       final int state;

       public Summary(Length position, int depth, int state) {
           this.position = position;
           this.depth = depth;
           this.state = state;
       }
   }
static final int ERROR_COST_PER_MISSING_TREE=110;
static final int ERROR_COST_PER_RECOVERY=500;
static final int ERROR_COST_PER_SKIPPED_CHAR=1;
static final int ERROR_COST_PER_SKIPPED_LINE=30;
static final int ERROR_COST_PER_SKIPPED_TREE=100;

private static <T extends Symbol> boolean isEquivalent(Subtree<T> left, Subtree<T> right) {
  return
    left == right ||
    (left!=null && right!=null &&
     left.symbol().equals(right.symbol())&&
     ((left.errorCost() == right.errorCost() ||
      (left.padding().offset() == right.padding().offset()&&
       left.size().offset() == right.size().offset()&&
       left.childCount() == right.childCount() &&
       left.extra() == right.extra() &&
       left.externalScannerStateEqual(right)))));
}
   private Entry base;
    private List<Head> heads = new ArrayList<>();
    private List<Slice> slices = new ArrayList<>();

Stack () {
  base = new Entry(null, Subtree.empty(), false, 1);
  clear();
}

private void addSlice(int original_version,
                                Entry entry, List<Subtree<T>> subtrees) {
  for (var i = this.slices.size() - 1; i + 1 > 0; i--) {
      var version = this.slices.get(i).version;
    if (this.heads.get(version).entry == entry) {
        slices.add(i+1, new Slice(subtrees, version));
      return;
    }
  }

    var version = addVersion(original_version, entry);
  slices.add(new Slice(subtrees, version));
}

private int addVersion(int originalVersion,
                                          Entry entry) {
       heads.add(new Head(entry, heads.get(originalVersion).nodeCountAtLastError, heads.get(i).lastExternalToken, Status.ACTIVE, null));
       return heads.size() -1;
}

boolean canMerge(int version1, int version2) {
    var head1 = heads.get(version1);
    var head2 = heads.get(version2);
  return
    head1.status == Status.ACTIVE &&
    head2.status == Status.ACTIVE &&
    head1.entry.state == head2.entry.state &&
    head1.entry.position.offset() == head2.entry.position.offset() &&
    head1.entry.errorCost == head2.entry.errorCost &&
    head1.lastExternalToken.externalScannerStateEqual(head2.lastExternalToken);
}

void clear() {
    heads.clear();
    heads
            .add(new Head(base, Subtree.empty() , null,0, null, Status.ACTIVE));

}

int copyVersion( int version) {
  heads.add(heads.get(version));
    var head = heads.get(heads.size()-1);
  head.summary = null;
  return heads.size() - 1;
}

int dynamicPrecdence(int version) {
  return heads.get( version).entry.dynamicPrecedence;
}

int errorCost(int version) {
    var head = heads.get( version);
    var result = head.entry.errorCost;
  if (
    head.status == Status.PAUSED ||
    (head.entry.state == 0 && head.entry.links.get(0).subtree == null)) {
    result += ERROR_COST_PER_RECOVERY;
  }
  return result;
}

List<Summary> getSummary(int version) {
  return heads.get( version).summary;
}

void halt(int version) {
  heads.get( version).status = Status.HALTED;
}

boolean isActive( int version) {
  return heads.get( version).status == Status.ACTIVE;
}

boolean isHalted( int version) {
  return heads.get( version).status == Status.HALTED;
}

boolean isPaused( int version) {
  return heads.get( version).status == Status.PAUSED;
}

void iterate(int version,
                      IterateCallback callback) {
    iterator( version, iterator -> { callback.accept(iterator.entry.state, iterator.subtreeCount); return EnumSet.noneOf(Action.class);}, -1);
}

private List<Slice> iterator(int version,
                             Function<StackIterator, Set<Action>> callback,
                             int goalSubtreeCount) {
       slices.clear();
    List<StackIterator> iterators = new ArrayList<>();

    var includeSubtrees = goalSubtreeCount >= 0;
  iterators.add(new StackIterator(heads.get(version).entry, true));

  while (iterators.size() > 0) {
    for (int i = 0, size = iterators.size(); i < size; i++) {
        var iterator = iterators.get(i);
        var entry = iterator.entry;

        var action = callback.apply(iterator);
        var should_pop = action.contains(Action.POP);
        var should_stop = action.contains(Action.STOP) || entry.links.isEmpty();

      if (should_pop) {
          var subtrees = iterator.subtrees;
        if (!should_stop) {
            subtrees = new ArrayList<>(subtrees);
        }
          Collections.reverse(subtrees);
        addSlice(
          version,
          entry,
          subtrees
        );
      }

      if (should_stop) {
        iterators.remove(i);
        i--; size--;
        continue;
      }

      for (var j = 1; j <= entry.links.size(); j++) {
        StackIterator next_iterator;
        Link link;
        if (j == entry.links.size()) {
          link = entry.links.get(0);
          next_iterator = iterators.get(i);
        } else {
          link = entry.links.get(j);
            var current_iterator = iterators.get(i);
          iterators.add(current_iterator);
          next_iterator =iterators.get(iterators.size()-1);
          next_iterator.subtrees = new ArrayList<>(next_iterator.subtrees);
        }

        next_iterator.entry = link.entry;
        if (link.subtree != null) {
          if (includeSubtrees) {
            next_iterator.subtrees.add(link.subtree);
          }

          if (!link.subtree.extra()) {
            next_iterator.subtreeCount++;
            if (!link.isPending) {
              next_iterator.isPending = false;
            }
          }
        } else {
          next_iterator.subtreeCount++;
          next_iterator.isPending = false;
        }
      }
    }
  }

  return this.slices;
}

Subtree<T> lastExternalToken(int version) {
  return heads.get( version).lastExternalToken;
}

boolean merge( int version1, int version2) {
  if (!canMerge(version1, version2)) return false;
    var head1 = heads.get(version1);
    var head2 = heads.get(version2);head2.entry.links.forEach( head1.entry::addLink);
  if (head1.entry.state == 0) {
    head1.nodeCountAtLastError = head1.entry.nodeCount;
  }
  heads.remove(version2);
  return true;
}

int nodeCountAtLastError(int version) {
    var head = heads.get( version);
  if (head.entry.nodeCount < head.nodeCountAtLastError) {
    head.nodeCountAtLastError = head.entry.nodeCount;
  }
  return head.entry.nodeCount - head.nodeCountAtLastError;
}

void pause(int version, T lookahead) {
    var head = heads.get( version);
  head.status = Status.PAUSED;
  head.lookaheadWhenPaused = lookahead;
  head.nodeCountAtLastError = head.entry.nodeCount;
}

List<Slice> popCount(int version, int count) {
  return iterator( version, iterator -> iterator.subtreeCount == count ? EnumSet.of(Action.POP, Action.STOP) : EnumSet.noneOf(Action.class), count);
}

List<Subtree<T>> popError( int version) {
    var entry = heads.get(version).entry;
  for (var link : entry.links) {
    if (link.subtree.isError()) {
        var found_error = new AtomicReference<Boolean>(false);
        var pop = iterator(version, iterator ->{
  if (iterator.subtrees.size() > 0) {
    if (!found_error.get() && iterator.subtrees.get(0).isError()) {
      found_error.set(true);
      return EnumSet.of(Action.POP , Action.STOP);
    } else {
      return EnumSet.of(Action.STOP);
    }
  } else {
    return EnumSet.noneOf(Action.class);
  }}
, 1);
      if (pop.size() > 0) {
        assert(pop.size() == 1);
        renumberVersion(pop.get(0).version, version);
        return pop.get(0).subtrees;
      }
      break;
    }
  }
  return Collections.emptyList();
}

List<Slice> popPending(int version) {
    var pop = iterator( version, iterator ->{
  if (iterator.subtreeCount >= 1) {
    if (iterator.isPending) {
      return EnumSet.of(Action.POP , Action.STOP);
    } else {
      return EnumSet.of(Action.STOP);
    }
  } else {
    return EnumSet.noneOf(Action.class);
  }
}, 0);
  if (pop.size() > 0) {
    renumberVersion(pop.get(0).version, version);
    pop.get(0).version = version;
  }
  return pop;
}

Length position(int version) {
  return heads.get( version).entry.position;
}

boolean printDotGraph(Language<T>language, PrintWriter output) {
    var id = 0;
  output.print("digraph stack {\n");
  output.print("rankdir=\"RL\";\n");
  output.print("edge [arrowhead=none]\n");

  List<Entry> visited_nodes = new ArrayList<>();
    final Deque<Entry> entries = new ArrayDeque<>();

    for (var i = 0; i < heads.size(); i++) {
        var head = heads.get(i);
    if (head.status == Status.HALTED) continue;

    output.format( "node_head_%d [shape=none, label=\"\"]\n", i);
    output.format( "node_head_%d . node_%d [", i, head.entry);

    if (head.status == Status.PAUSED) {
      output.print( "color=red ");
    }
    output.format(
      "label=%d, fontcolor=blue, weight=10000, labeltooltip=\"node_count: %d\nerror_cost: %d",
      i,
      ts_stack_node_count_since_error(i),
      ts_stack_error_cost(i)
    );

    if (head.lastExternalToken != null) {
        output.print("\nexternal_scanner_state:");
      Stream.of(head.lastExternalToken.external_scanner_state)//
        .forEach(d -> output.printf(" %2Xd", d));
    }

    output.print( "\"]\n");
    entries.offer(head.entry);
  }

  while (!entries.isEmpty()) {
      var entry = entries.poll();


      if (visited_nodes.stream().anyMatch(entry::equals)) {
          continue;
      }

      output.printf("node_%d [", ++id);
      if (entry.state == 0) {
        output.print("label=\"?\"");
      } else if (
        entry.links.size()== 1 &&
        entry.links.get(0).subtree!= null &&
        entry.links.get(0).subtree.extra()
      ) {
        output.print("shape=point margin=0 label=\"\"");
      } else {
        output.printf( "label=\"%d\"", entry.state);
      }

      output.printf(
        " tooltip=\"position: %u,%u\nnode_count:%u\nerror_cost: %u\ndynamic_precedence: %d\"];\n",
        entry.position.point().row(),
        entry.position.point().column(),
        entry.nodeCount,
        entry.errorCost,
        entry.dynamicPrecedence
      );

      for (var link : entry.links) {
        output.printf("node_%d . node_%p [", id, link.entry);
        if (link.isPending) output.print("style=dashed ");
        if (link.subtree!=null && link.subtree.extra()) output.print("fontcolor=gray ");

        if (link.subtree == null) {
          output.print("color=red");
        } else {
          output.print("label=\"");
            var quoted = link.subtree.isVisible() && !link.subtree.named();
          if (quoted) output.print("'");
          output.print(language.name(link.subtree.symbol().apply(T::name, SpecialSymbol::name).replace("\"", "\\\""));
          if (quoted) output.print("'");
          output.print("\"");
          output.printf(
            "labeltooltip=\"error_cost: %d\ndynamic_precedence: %d\"",
            link.subtree.errorCost(),
            link.subtree.dynamicPrecedence()
          );
        }

        output.print("];\n");

        entries.offer(link.entry);

      }

      visited_nodes.add( entry);
    }

  output.print("}\n");
  return true;
}

void push(int version, Subtree<T> subtree,
                   boolean pending, int state) {
    var head = heads.get( version);
    var new_node = new Entry(head.entry, subtree, pending, state);
  if (subtree == null) head.nodeCountAtLastError = new_node.nodeCount;
  head.entry = new_node;
}

void recordSummary(int version, int max_depth) {
    List<Summary> summary = new ArrayList<>();
  iterator( version, iterator -> {
      var state = iterator.entry.state;
      var depth = iterator.subtreeCount;
  if (depth > max_depth) return EnumSet.of(Action.STOP);
  for (var i = summary.size() - 1; i + 1 > 0; i--) {
      var entry = summary.get(i);
    if (entry.depth < depth) break;
    if (entry.depth == depth && entry.state == state) return EnumSet.noneOf(Action.class);
  }
  summary.add(new Summary(iterator.entry.position, depth, state));
  return EnumSet.noneOf(Action.class);
  }, -1);
  heads.get(version).summary = summary;
}

void renumberVersion(int v1, int v2) {
  if (v1 == v2) return;
  assert(v2 < v1);
    var source_head = heads.get(v1);
    var target_head = heads.get(v2);
  if (target_head.summary != null && source_head.summary == null) {
    source_head.summary = target_head.summary;
    target_head.summary = null;
  }
  heads.set(v2, new Head(source_head.entry, source_head.lastExternalToken, source_head.summary, source_head.nodeCountAtLastError, source_head.lookaheadWhenPaused, source_head.status));
  heads.remove(v1);
}

T resume(int version) {
    var head = heads.get( version);
  assert(head.status == Status.PAUSED);
    var result = head.lookaheadWhenPaused;
  head.status = Status.ACTIVE;
  head.lookaheadWhenPaused = null;
  return result;
}

void setLastExternalToken(int version, Subtree<T> token) {
    var head = heads.get( version);
  head.lastExternalToken = token;
}

int state(int version) {
  return heads.get( version).entry.state;
}

void swapVersions(int v1, int v2) {
    var temporary_head = heads.get(v1);
  heads.set(v1, heads.get(v2));
  heads.set(v2, temporary_head);
}

List<Slice> ts_stack_pop_all( int version) {
  return iterator( version, iterator ->iterator.entry.links.isEmpty()  ? EnumSet.of(Action.POP) : EnumSet.noneOf(Action.class) , 0);
}

int versionCount() {
  return heads.size();
}

}
