package gulik.dolichos;

import gulik.urad.Type;

public class ColumnDefinition {
    private String name;
    private Type type;
    private boolean isPrimaryKey;

    public ColumnDefinition(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
