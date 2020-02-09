package gulik.urad.where;

public class False extends Clause {
    private static final False myInstance = new False();
    public static False instance() {
        return myInstance;
    }
}
