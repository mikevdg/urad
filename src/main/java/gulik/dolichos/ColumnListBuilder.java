package gulik.dolichos;

import java.util.ArrayList;
import java.util.List;

import gulik.demo.NotImplementedException;
import gulik.urad.Type;
import gulik.urad.tableColumn.IntegerColumn;
import gulik.urad.tableColumn.StringColumn;
import gulik.urad.tableColumn.TableColumn;
import gulik.urad.tableColumn.TimestampColumn;

/** Fluent API helper to create lists of columns. */
public class ColumnListBuilder {
    private List<TableColumn> columns = new ArrayList<>(20);

    /** Add another column. */
    public ColumnListBuilder add(String name, Type type) {
        TableColumn newColumn;

        switch (type) {
            case Integer:
                newColumn = new IntegerColumn();
                break;
            case String:
                newColumn = new StringColumn();
                break;
            case Timestamp:
                newColumn = new TimestampColumn();
            case Boolean:
            default:
                throw new NotImplementedException();
        }

        newColumn
                .setName(name)
                .setType(type);
        columns.add(newColumn);
        return this;
    }

    /** Define the primary key for this table. */
    public ColumnListBuilder pk(String one) {
        for (TableColumn each : columns) {
            if (each.getName().equals(one)) {
                each.setPrimaryKey(true);
            }
        }

        return this;
    }

    /** Define the primary key for this table. */
    public ColumnListBuilder pk(String one, String two) {
        for (TableColumn each : columns) {
            if (each.getName().equals(one)) {
                each.setPrimaryKey(true);
            }
            if (each.getName().equals(two)) {
                each.setPrimaryKey(true);
            }
        }

        return this;
    }

    public List<TableColumn> build() {
        return columns;
    }
}
