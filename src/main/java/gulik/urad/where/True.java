package gulik.urad.where;

public class True extends Clause {
    private static final True myInstance = new True();
    public static True instance() {
        return myInstance;
    }
}
