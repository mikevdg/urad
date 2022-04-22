package gulik.urad.queryables;

import gulik.urad.Query;
import gulik.urad.Table;

/** I am a "utility" class to convert a Query into a Table. I perform the given query on whatever
 * data you set me up with.
 * 
 * TODO: Am I just a "Table"?
 */
public interface Queryable {

    /** Perform the given query and return a result. */
    Table query(Query q);
}
