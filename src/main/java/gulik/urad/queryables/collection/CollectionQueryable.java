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

public class CollectionQueryable implements Queryable {
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
        return new CollectionTable(q.getFrom(), source, q);
    }
}
