package gulik.urad;


// TODO: should I be a wrapper class around basic types?
public interface Value {
    public static IntegerValue value(Integer i);
    public static StringValue value(String s);
    // etc?
}
