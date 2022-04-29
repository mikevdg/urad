package gulik.urad.queryables.collection;

import gulik.urad.Query;
import gulik.urad.Row;
import gulik.urad.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/** I'm returned if a query is performed with no data present - meaning that we can't derive columns. */
public class EmptyResultSet extends CollectionResultSet {

    public EmptyResultSet(Table definition, Object[] source, Query query) {
        super(definition, source, query);
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
