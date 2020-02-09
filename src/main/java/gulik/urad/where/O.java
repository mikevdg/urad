package gulik.urad.where;

/** I am an And, Or or Not. */
public abstract class O extends Clause {


    public O(Clause left, Clause right) {
        super(left, right);
    }

    protected O() {
    }
}
