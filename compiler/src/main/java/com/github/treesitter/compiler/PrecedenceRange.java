
class PrecedenceRange {

  int min;
  int max;
  boolean empty;

PrecedenceRange() { this (0, 0, true); }

PrecedenceRange(int min, int max) {
    this(min, max,false); }

PrecedenceRange(int value) {
    this(value, value, false); }
private PrecedenceRange(int min, int max, boolean empty) {
this.min=min; this.max = max;this.empty = empty;
}

void add(int new_value) {
  if (empty) {
    min = new_value;
    max = new_value;
    empty = false;
  } else {
    if (new_value < min)
      min = new_value;
    else if (new_value > max)
      max = new_value;
  }
}

void add(PrecedenceRange other) {
  if (!other.empty) {
    add(other.min);
    add(other.max);
  }
}

boolean lessThan(PrecedenceRange other) {
  if (empty)
    return !other.empty;
  else
    return (min < other.min && max <= other.min) ||
           (min == other.min && max < other.max);
}

boolean isEqual(PrecedenceRange other) {
  return (empty == other.empty) && (min == other.min) && (max == other.max);
}

}
