package gulik.dolichos;

// TODO - not used yet.
public class Factory {
    public static class ColumnBuilder {
        // public ColumnBuilder column(String name, ColumnType type) {}
        //public ColumnDefinition build() {}
    }

    public static ColumnBuilder columns() {
        return new ColumnBuilder();
    }
}
