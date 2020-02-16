package gulik.urad;

import gulik.urad.value.Value;

/** I am a row from a Table. I'm a "dumb" object; a Table is a "smart" object. All of
 * the column definitions and intelligence is in the Table implementation.
 *
 * Don't rely on me being an immutable object. It is implementation specific, but I might be re-used
 * in the next iteration.
 */
public interface Row {
    public Value get(int columnNum);
    public void set(int columnNum, Value v);
}
