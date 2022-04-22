package gulik.dolichos;

import gulik.urad.Query;
import gulik.urad.Table;

/* TODO: Is this just a "Table"?

Do we need to have a differentiation between a table as a ResultSet and a table as a TableDefinition?

If you run a query, what you get back is tabular. Can it be a table? Is there a difference between a table and a query result?

Tables have concrete implementations. Query results are generated. I guess they share the same interface.

*/

public interface ODataEntitySet {
    public String getName();
    public ColumnDefinition[] getColumns();
    public Table query(Query q);
    public Table create(Table t);
    public Table update(Table t);
    public Table delete(Table t);
}
