package gulik.urad.value;

import java.util.Objects;

public abstract class Value {
    public static Value NULL = new NullValue();

    public static Value of(Object something) {
        if (null==something) return NULL;
        if (something instanceof String) {
            return new StringValue((String)something);
        }
        if (something instanceof Integer) {
            return new IntegerValue((Integer)something);
        }
        throw new RuntimeException("Can't make a Value out of "+ Objects.toString(something));
    }

    public abstract Object value();
}
