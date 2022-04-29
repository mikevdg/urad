package gulik.urad;

import java.util.List;

import gulik.urad.tableColumn.TableColumn;

/* TODO: Is this just a "Table"?

Do we need to have a differentiation between a table as a ResultSet and a table as a TableDefinition?

If you run a query, what you get back is tabular. Can it be a table? Is there a difference between a table and a query result?

Tables have concrete implementations. Query results are generated. I guess they share the same interface.

*/

public interface Table {
    public String getName();
    public List<TableColumn> getColumns();
    public ResultSet query(Query q);
    public ResultSet create(ResultSet t);
    public ResultSet update(ResultSet t);
    public ResultSet delete(ResultSet t);
}