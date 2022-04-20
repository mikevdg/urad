package gulik.urad.queryables.collection;

import gulik.urad.*;
import gulik.urad.queryables.Queryable;
import gulik.urad.value.Value;

import java.util.*;
import java.util.stream.Collectors;

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

    public CollectionQueryable(Collection c) {
        if (null==c) throw new NullPointerException();
        this.source = c;
    }

    @Override
    public Table query(Query q) {
        if (source.isEmpty()) {
            return new EmptyTable(q.getFrom(), source.toArray(new Object[source.size()]), q);
        }
        return new CollectionTable(q.getFrom(), source.toArray(new Object[source.size()]), q);
    }
}
