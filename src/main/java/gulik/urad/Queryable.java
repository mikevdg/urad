package gulik.urad;

public interface Queryable {

    /** Perform the given query and return a result. */
    Table query(Query q);
}
