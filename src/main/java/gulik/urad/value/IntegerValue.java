package gulik.urad.value;

public class IntegerValue extends Value {
    private Integer value;

    public IntegerValue(Integer value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
