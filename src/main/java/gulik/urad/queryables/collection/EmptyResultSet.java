package gulik.urad.queryables.collection;

import gulik.urad.Query;
import gulik.urad.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/** I'm returned if a query is performed with no data present. */
public class EmptyResultSet extends CollectionResultSet {

    public EmptyResultSet(Query query, Object[] source) {
        super(query, source);
    }

    @Override
    public Stream<Row> stream() {
        return new ArrayList<Row>().stream();
    }

    @Override
    public Iterator<Row> iterator() {
        return new ArrayList<Row>().iterator();
    }

    @Override
    public boolean hasCount() {
        return true;
    }

    @Override
    public Integer getCount() {
        return 0;
    }
}
