package gulik.urad;

import java.util.List;
import java.util.stream.Stream;

import gulik.urad.queryColumn.QueryColumn;
import gulik.urad.value.Value;

/**
 * I am tabular data which is the result of a query.
 */
public interface ResultSet extends Iterable<gulik.urad.Row> {
    /** Return my SQL name */
    String getCode();

    /** Return my human-readable name. */
    String getName();

    /** Return a lengthy diatribe of what I am. */
    String getDescription();

    List<QueryColumn> getColumns();
    QueryColumn getColumnByName(String name);
    List<QueryColumn> getPrimaryKey();
    Stream<Row> stream();

    /** The "count" concept in OData differs from SQL. In an OData response, the count of the entire query
     * is returned *with* some items of the query. The count ignores $top and $skip.
     *
     * If you want a value here, you need to use selectCount() when you make the Query.
     * @return
     */
    boolean hasCount();
    Integer getCount();

    public Row insert(Row row);
    public Row update(Value key, Row row);
    public void delete(Value key);

}
