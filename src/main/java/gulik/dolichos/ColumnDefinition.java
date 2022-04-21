package gulik.dolichos;

public class ColumnDefinition {
    private String name;

    public ColumnDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // TODO
    public String getType() {
        return "type";
    }
}
