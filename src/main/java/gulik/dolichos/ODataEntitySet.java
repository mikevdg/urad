package gulik.dolichos;

import gulik.urad.Query;
import gulik.urad.Table;

public interface ODataEntitySet {
    public String getName();
    public ColumnDefinition[] getColumns();
    public Table query(Query q);
    public Table create(Table t);
    public Table update(Table t);
    public Table delete(Table t);
}
