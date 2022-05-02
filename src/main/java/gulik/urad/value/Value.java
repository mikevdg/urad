package gulik.urad.value;

import java.util.Date;
import java.util.Objects;

public abstract class Value implements Comparable {
    public static Value NULL = new NullValue();

    public static Value of(Object something) {
        if (null==something) return NULL;
        if (something instanceof String) {
            return new StringValue((String)something);
        }
        if (something instanceof Integer) {
            return new IntegerValue((Integer)something);
        }
        if (something instanceof Date) {
            // TODO: ... timestamp?
            return new DateValue((Date)something);
        }
        if (something instanceof Boolean) {
            return new BooleanValue((Boolean)something);
        }
        throw new RuntimeException("Can't make a Value out of "+ Objects.toString(something));
    }

    public abstract Object value();

    public int compareTo(Object x) {
        if (null==x) {
            return 1;
        }
        Value to = (Value)x;
        if (!(value() instanceof Comparable)) {
            throw new NotComparable();
        }
        if (!(to.value() instanceof Comparable)) {
            throw new NotComparable();
        }
        return ((Comparable)value()).compareTo(((Comparable)to.value()));
    }
}
