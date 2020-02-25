package gulik.urad;

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
    private String from;
    private List<String> selects = new ArrayList<>();
    private List<Clause> whereClauses = new ArrayList<>();
    private List<String> orderBy = new ArrayList<>();
    private Integer top;
    private Integer skip;

    /** A special type of query - don't retrieve rows, but return a Table object
     * so that the caller can inspect the column definitions.
     * @return
     */
    public static Query queryDefinition() {
        return new Query(); // TODO
    }

    /* Methods for building a query: */

    /** Add the given column to my result. You can call me multiple times to add more columns.
     * Calling me multiple time with the same column path is benign. */
    public Query select(String columnPath){
        selects.add(columnPath);
        return this;
    }

    /** OData has this concept of "select by the ID" where we are expected to know what
     * the ID is.
     * @param values The values for the IDs of this table.
     */
    public Query selectById(Value[] values){/* TODO */ throw new NotImplemented();}

    // TODO public Query distinct() ???

    /** Set my target. I can only be called once or I throw an IllegalArgumentException.
       Sometimes this is redundant; it depends on the implementation of the Queryable.
     */
    public Query from(String entityName) {
        this.from = entityName;
        return this;
    }

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
        orderBy.add(columnPath);
        return this;
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

    public List<String> getSelects(){
        return selects;
    }

    public String getFrom(){
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

    public List<String> getOrderBys(){
        return orderBy;
    }

    public int getTop(){
        return top;
    }

    public int getSkip(){
        return skip;
    }
}
