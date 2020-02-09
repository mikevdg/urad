package gulik.urad;

import gulik.urad.value.Value;
import gulik.urad.where.WhereClause;

import java.util.List;

/** I am a stand-alone query. I don't hold references to any other objects. You pass me to
 * a Queryable.
 *
 * These methods can all be invoked in any order.
 *
 */
public class Query {

    /* Methods for building a query: */

    /** Add the given column to my result. You can call me multiple times to add more columns.
     * Calling me multiple time with the same column path is benign. */
    public Query select(String columnPath);


    /** OData has this concept of "select by the ID" where we are expected to know what
     * the ID is.
     * @param values The values for the IDs of this table.
     */
    public Query selectById(Value[] values);

    // TODO public Query distinct() ???

    /** Set my target. I can only be called once or I throw an IllegalArgumentException.
       Sometimes this is redundant; it depends on the implementation of the Queryable.
     */
    public Query from(String entityName) throws IllegalArgumentException;

    /** Add a condition to the query. See the WhereClause class for a bunch of useful
    factory methods. Calling me multiple times just adds more conditions.
     */
    public Query where(WhereClause clause);

    /** Sort the results. Calling me multiple times adds more columns to sort by.
     */
    public Query orderBy(String columnPath);

    /** Only show this many results: */
    public Query top(int howMany);

    /** Skip this many results before showing me more: */
    public Query skip(int howMany);

    // TODO: group by, having.

    /* Methods for "business logic" that changes the query: */

    /** Remove a column in the select list from me.
     */
    public Query removeSelect(String columnPath) throws IndexOutOfBoundsException;

    /** Remove one of the top-level WHERE clauses. It must be a top-level clause.
     */
    public Query removeWhere(WhereClause clause);

    /** Remove the given column path from the ORDER BY list.
     */
    public Query removeOrderBy(String columnPath);


    /* Methods used by the Queryable. */

    public List<String> getSelects();

    public String getFrom();

    /** Return all the WhereClauses. These can be considered to be all in conjunction with each
     * other. Keep in mind that these are a tree structures of boolean operators.
     *
     * I return the actual instances used. If a business logic person wants to modify them, they will
     * be modified in me too.
     */
    public List<WhereClause> getWhereClauses();

    public List<String> getOrderBys();

    public int getTop();
    public int getSkip();
}
