package gulik.urad.where;

public class ColumnRef extends Clause {
    private String columnPath;

    public ColumnRef(String columnPath) {
        this.columnPath=columnPath;
    }
}
