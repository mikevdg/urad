package gulik.urad.where;

public class Comparison extends O {
    private Comparator comparator;

    public Comparison(Clause left, Comparator comparator, Clause right) {
        this.left = left;
        this.comparator = comparator;
        this.right = right;
    }

    public Comparison(Comparator unaryOperator, ColumnRef columnRef) {
        this.left=columnRef;
        this.comparator = unaryOperator;
    }
}
