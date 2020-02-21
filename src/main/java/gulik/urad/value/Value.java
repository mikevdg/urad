package gulik.urad.value;

public abstract class Value {
    public static Value NULL = new NullValue();

    public abstract Object value();
}
