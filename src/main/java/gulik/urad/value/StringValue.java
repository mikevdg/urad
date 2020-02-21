package gulik.urad.value;

public class StringValue extends Value {
    String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }
}
