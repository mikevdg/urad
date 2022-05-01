package gulik.urad;

import java.util.List;
import java.util.stream.Stream;

import gulik.urad.queryColumn.QueryColumn;
import gulik.urad.value.Value;

/**
 * I am tabular data which is the result of a query.
 */
public abstract class ResultSet implements Iterable<gulik.urad.Row> {  
    protected final Query query;
    private Integer count; 

    protected ResultSet(Query q) {
        this.query = q;
    }

    /** Use this to get data from me. */
    public abstract Stream<Row> stream();

    /** If I'm backed by an SQL database, this is the name of the SQL table. */
    public String getName() {
        // TODO: There is a "title" used in OData.
        return getTable().getName();
    }

    /** You show this to the user. */
    public String getTitle() {
        return getTable().getName();
    }

    /* Return a diatribe describing what I am. */
    public String getDescription() {
        // TODO
        return getTable().getName();
    }

    public List<QueryColumn> getColumns() {
        return query.getSelects();
    }

    public QueryColumn getColumnByName(String name) {
        for (QueryColumn each : query.getSelects()) {
            if (each.getName().equals(name)) {
                return each;
            }
        }
        throw new IndexOutOfBoundsException("Could not find column " + name + " in query " + query.toString());
    }

    public int getColumnNumber(String name) {
        int i = 0;
        for (QueryColumn each : query.getSelects()) {
            if (each.getName().equals(name)) {
                return i;
            }
            i++;
        }
        throw new IndexOutOfBoundsException("Could not find column " + name + " in query " + query.toString());
    }

    public List<QueryColumn> getPrimaryKey() {
        return query.getPrimaryKey();
    }

    public Table getTable() {
        return query.getTable();
    }

    /** The "count" concept in OData differs from SQL. In an OData response, the count of the entire query
     * is returned *with* some items of the query. The count ignores $top and $skip.
     *
     * If you want a value here, you need to use selectCount() when you make the Query.
     * @return
     */
    public boolean hasCount() {
        return null != count;
    }

    public Integer getCount() {
        return count;
    }

    public abstract Row insert(Row row);
    public abstract Row update(Value key, Row row);
    public abstract void delete(Value key);

}
