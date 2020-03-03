package gulik.urad.value;

public class NullValue extends Value {
    @Override
    public Object value() {
        return null;
    }

    @Override
    public String toString() {
        return "Null";
    }
}
