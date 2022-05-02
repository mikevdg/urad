package gulik.urad.value;

public class BooleanValue extends Value {
    Boolean value;

    public BooleanValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        if (null==value) {
            return "";
        } else {
            return value.toString();
        }
    }
}
