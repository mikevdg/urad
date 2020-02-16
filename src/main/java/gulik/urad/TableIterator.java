package gulik.urad;

import gulik.urad.impl.Row;

import java.util.function.Consumer;

public interface TableIterator {
    /** Iterate over all the rows, performing the given action. */
    public void iterate(Consumer<Row> action);

    /* * Iterate from this row to that row. */
    // public void iterator(int skip, int top, Consumer<Row> action);

    /* * Get the one and only one row. */
    // public Row getOne() throws TooMany, NotEnough;

}
