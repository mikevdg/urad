package gulik.urad;

import gulik.urad.exceptions.NotImplemented;
import gulik.urad.queryColumn.QueryColumn;
import gulik.urad.tableColumn.TableColumn;
import gulik.urad.value.Value;
import gulik.urad.where.Clause;

import java.util.ArrayList;
import java.util.List;

/** I am a stand-alone query. I don't hold references to any other objects. You pass me to
 * a Queryable.
 *
 * These methods can all be invoked in any order.
 *
 */
public class Query {
    private final Table from;
    private List<QueryColumn> selects = new ArrayList<>();
    private List<Clause> whereClauses = new ArrayList<>();
    private List<QueryColumn> orderBy = new ArrayList<>();
    private Integer top;
    private Integer skip;
    private boolean isCount=false;

    public Query(Table from) {
        this.from = from;
    }

    public ResultSet fetch() {
        if (this.getSelects().isEmpty()) {
            selectAll();
        }

        return from.fetch(this);
    }
    /* Methods for building a query: */

    /** Add the given column to my result. You can call me multiple times to add more columns.
     * Calling me multiple time with the same column path is benign. */
    public Query select(String columnPath){
        // TODO: parse the columnPath.
        for (TableColumn each : from.getColumns()) {
            if (each.getName().equals(columnPath)) {
                selects.add(QueryColumn.from(each));
                renumberColumns();
                return this;
            }
        }
        throw new IndexOutOfBoundsException("Could not find column \""+columnPath+"\" in table "+this.getTable().getName());
    }

    /** Do a "SELECT * FROM ..." */
    public Query selectAll() {
        for (TableColumn each : from.getColumns()) {
            selects.add(QueryColumn.from(each));
        }
        renumberColumns();
        return this;
    }

    private void renumberColumns() {
        int i = 0;
        for (QueryColumn each : selects) {
            each.setColumnIndex(i);
            i++;
        }
    }

    /** OData has this concept of "select by the ID" where we are expected to know what
     * the ID is.
     * @param values The values for the IDs of this table.
     */
    public Query selectById(Value[] values){/* TODO */ throw new NotImplemented();}

    // TODO public Query distinct() ???

    /** Add a condition to the query. See the WhereClause class for a bunch of useful
    factory methods. Calling me multiple times just adds more conditions.
     */
    public Query where(Clause clause){
        this.whereClauses.add(clause);
        return this;
    }

    /** Sort the results. Calling me multiple times adds more columns to sort by.
     */
    public Query orderBy(String columnPath){
        // TODO: parse the columnPath.
        for (TableColumn each : this.from.getColumns()) {
            if (each.getName().equals(columnPath)) {
                orderBy.add(QueryColumn.from(each));
                return this;
            }
        }
        throw new IndexOutOfBoundsException("Could not find column "+columnPath+" in "+from.getName());
    }

    /** Only show this many results: */
    public Query top(int howMany){
        this.top = howMany;
        return this;
    }

    /** Skip this many results before showing me more: */
    public Query skip(int howMany){
        this.skip = howMany;
        return this;
    }

    // TODO: group by, having.

    /* Methods for "business logic" that changes the query: */

    /** Remove a column in the select list from me.
     */
    public Query removeSelect(String columnPath) throws IndexOutOfBoundsException{
        selects.remove(columnPath);
        return this;
    }

    /** Remove one of the top-level WHERE clauses. It must be a top-level clause.
     */
    public Query removeWhere(Clause clause){
        whereClauses.remove(clause);
        return this;
    }

    /** Remove the given column path from the ORDER BY list.
     */
    public Query removeOrderBy(String columnPath){
        orderBy.remove(columnPath);
        return this;
    }

    /* Methods used by the Queryable. */

    public List<QueryColumn> getSelects(){
        return selects;
    }

    public List<QueryColumn> getPrimaryKey() {
        List<QueryColumn> result = new ArrayList<>();
        for (TableColumn each : from.getColumns()) {
            if (each.isPrimaryKey()) {
                result.add(QueryColumn.from(each));
            }
        }
        return result;
    }

    public boolean isSelectAll() {
        return selects.isEmpty();
    }

    public Table getFrom(){
        return from;
    }

    /** Return all the WhereClauses. These can be considered to be all in conjunction with each
     * other. Keep in mind that these are a tree structures of boolean operators.
     *
     * I return the actual instances used. If a business logic person wants to modify them, they will
     * be modified in me too.
     */
    public List<Clause> getWhereClauses(){
        return whereClauses;
    }

    public List<QueryColumn> getOrderBys(){
        return orderBy;
    }

    public int getTop(){
        return top;
    }

    public int getSkip(){
        return skip;
    }

    public boolean hasOrderBys() {
        return !orderBy.isEmpty();
    }

    public void selectCount() {
        isCount = true;
    }

    public Table getTable() {
        return this.from;
    }
}
