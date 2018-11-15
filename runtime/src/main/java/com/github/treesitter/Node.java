package com.github.treesitter;

public class Node<T  extends Symbol> implements Range {
	@Override
	public Length startLength() {
		return startLength;
	}

	@Override
	public Length endLength() {
		return endLength;
	}

	private final Tree<T> root;
	private final T alias;
	private final Length startLength;
	private final Length endLength;
	private final Subtree<T> id;

	public Node(Tree<T> root, Subtree<T> id, Length position, T alias) {
		this.alias = alias;
		this.root = root;
		this.id = id;
	}

	T symbol() {
		return alias;
	}

	String string() {
		return id.string(root.language(), false	);
	}

boolean eq( Node<T>other) {
	return root == other.root && id == other.id;
}

	boolean isNull() {return id == null;}

	boolean isNamed() {
		return alias == null ? id.named() : root.language().metadata(alias).isNamed();
	}

	boolean isMissing() { return id.missing();}

	boolean has_changes() {
		return id.hasChanges();
	}

	boolean hasError() {
		return id.errorCost()> 0;
	}

	Node<T> parent();

Node<T> child( uint32_t);

Node<T> named_child( uint32_t);

	int childCount() {
		return id.apply(new SubtreeVisitor<T, Integer>() {

			@Override
			public Integer apply(SubtreeNonTerminal<T> nonTerminal) {
				// TODO Auto-generated method stub
				return nonTerminal.visibleChildCount();
			}

			@Override
			public Integer apply(SubtreeExternalTerminal<T> terminal) {
				return 0;
			}

			@Override
			public Integer apply(SubtreeError<T> error) {
				return 0;
			}
		});
	}

	int namedChildCount() {
		return id.apply(new SubtreeVisitor<T, Integer>() {

			@Override
			public Integer apply(SubtreeNonTerminal<T> nonTerminal) {
				return nonTerminal.namedChildCount();
			}

			@Override
			public Integer apply(SubtreeExternalTerminal<T> terminal) {
				return 0;
			}

			@Override
			public Integer apply(SubtreeError<T> error) {
				return 0;
			}
		});
	}

	Node next_sibling();

	Node next_named_sibling();

	Node prev_sibling();

	Node prev_named_sibling();

Node first_child_for_byte( uint32_t);

Node first_named_child_for_byte( uint32_t);

Node descendant_for_byte_range( uint32_t, uint32_t);

Node named_descendant_for_byte_range( uint32_t, uint32_t);

Node descendant_for_point_range( Point, Point);

Node named_descendant_for_point_range( Point, Point);

	void edit(InputEdit edit);

	@Override
	public Point startPoint() {
		return startLength.point();
	}

	@Override
	public Point endPoint() {
		return endLength.point();
	}

	@Override
	public int startOffset() {
		return startLength.offset();
	}

	@Override
	public int endOffset() {
		return endLength.offset();
	}

}
