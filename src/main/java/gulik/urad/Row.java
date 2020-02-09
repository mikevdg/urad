package gulik.urad;

/** I'm basically just an array. All of my important information, such as how many columns I have
 * and what type they are are in my owning Table object.
 *
 * Don't rely on me being an immutable object. It is implementation specific, but I might be re-used
 * in the next iteration.
 */
public class Row {
    Value get(int columnNum);
    // TODO: set?
}
