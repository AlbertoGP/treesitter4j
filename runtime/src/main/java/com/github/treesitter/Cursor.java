public class Cursor {
  private Object tree;
  private Object id;
  private int[] context = new int[2];

  public void reset(Node node);

  Node<T> current();

  boolean goto_parent();

  boolean goto_next_sibling();

  boolean goto_first_child();

  long goto_first_child_for_byte(int offset);
}
