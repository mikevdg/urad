package gulik.urad.queryables;

import gulik.urad.Query;
import gulik.urad.Table;

public interface Queryable {

    /** Perform the given query and return a result. */
    Table query(Query q);
}
