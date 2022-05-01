package gulik.urad.queryables.collection;

import java.util.Collection;

import gulik.urad.Query;
import gulik.urad.ResultSet;
import gulik.urad.Table;
import gulik.urad.queryables.Queryable;

/** I let you perform queries on standard Java Collections, Lists and Maps.
 *
 */
public class CollectionQueryable implements Queryable {
    /* Selecting and Filtering are handled by a CollectionTable. It can
    do selecting and filtering while iterating over a collection.

    Ordering needs to be done here before we give a collection table a
    collection to iterate over.
     */
    private final Collection source;

    public CollectionQueryable(Table s, Collection c) {
        if (null==c) throw new NullPointerException();
        this.source = c;
    }

    @Override
    public ResultSet query(Query q) {
        if (source.isEmpty()) {
            return new EmptyResultSet(q, source.toArray(new Object[source.size()]));
        }
        return new CollectionResultSet(q, source.toArray(new Object[source.size()]));
    }
}
