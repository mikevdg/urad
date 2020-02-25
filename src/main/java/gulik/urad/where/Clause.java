package gulik.urad.where;

import gulik.urad.value.IntegerValue;
import gulik.urad.value.StringValue;

/** I am a boolean operator or comparison. I am anything that can be put in a WHERE clause. */
public abstract class Clause {
    public static Comparison equal(String columnPath, String value){
        return new Comparison(
                new ColumnRef(columnPath),
                Comparator.equal,
                new Constant(new StringValue(value))
        );
    }
    public static Comparison equal(String columnPath, Integer value) {
        return new Comparison(
                new ColumnRef(columnPath),
                Comparator.equal,
                new Constant(new IntegerValue(value))
        );
    }
    public static Comparison like(String columnPath, String value){
        return new Comparison(
                new ColumnRef(columnPath),
                Comparator.like,
                new Constant(new StringValue(value))
        );
    }
    public static Comparison notNull(String columnPath){
        return new Comparison(
                Comparator.isNotNull,
                new ColumnRef(columnPath)
        );
    }

    /** Use this as a placeholder for a WhereClause that you removed. */
    public static True true_() {
        return True.instance();
    }

    /** Use this as the only clause if you don't want any rows in a table. This is
     * used when you only want the column definitions.
     */
    public static False false_() {
        return False.instance();
    }


    public static And and(O left, O right){
        return new And(left, right);
    }

    public static Or or(O left, O right){
        return new Or(left, right);
    }

    public static Not not(O notThis){
        return new Not(notThis);
    }

    protected Clause left;
    protected Clause right; // Not used in unary operators.
    // The comparator depends on which subclass I am.

    public Clause(Clause left, Clause right) {
        this.left = left;
        this.right = right;
    }

    protected Clause() {};
}
