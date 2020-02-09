package gulik.urad.where;

/** I am a boolean operator or comparison. I am anything that can be put in a WHERE clause. */
public abstract class Clause {
    protected Clause left;
    protected Clause right; // Not used in unary operators.
    // The comparator depends on which subclass I am.

    public Clause(Clause left, Clause right) {
        this.left = left;
        this.right = right;
    }

    protected Clause() {};
}
