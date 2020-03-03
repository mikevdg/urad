package gulik.urad.queryables.collection;

import gulik.urad.Column;
import gulik.urad.Query;
import gulik.urad.Row;
import gulik.urad.Table;
import gulik.urad.queryables.Queryable;
import gulik.urad.value.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
            return new EmptyTable(q.getFrom());
        }

        if (q.hasOrderBys()) {
            return new CollectionTable(q.getFrom(), sorted(source, q), q);
        } else {
            return new CollectionTable(q.getFrom(), source, q);
        }
    }

    /** Return a sorted version of c, sorted by the orderBys in the Query q.*/
    private List sorted(Collection c, Query q) {
        /** Difficulty: we must sort by:
         * 1. A property which could be a direct member of c, or could be down a path in c.
         * 2. More properties of c after that.
         */
        Collection result = c.copy();
        List<Comparitor> comparitors = q.getOrderBys().stream().map(each ->
                asComparitor(each, c));
        // for each of the orderBys,
        for (int orderByIndex = 0; orderByIndex=q.getOrderBys().size(); i++) {
            int from=0;
            while (from < result.size()) {
                to = getOrderByRangeFrom(comparitors, orderByIndex, from, result);
                Comparitor cm = getOrderByComparitor(c, q.getOrdersBy().get(orderByIndex));
                Arrays.sort(result, from, to, cm);
            }
        }
    }

    private Comparitor asComparitor(String orderBy, Collection in) {
        throw new NotImplemented();
    }

    private int getOrderByRangeFrom(List<Comparitors> ordersBy, int orderByFrom int orderByTo, int from, Collection c) {
        int f = from;
        for (int i=orderByFrom; i<orderByTo; i++) {

        }

    }
}
