package com.odata.urad;

import java.util.List;
import java.util.function.Consumer;

/** I am tabular data which is the result of a query.
 *
  */
public interface Table {
    /** Return my SQL name */
    String getCode();

    /** Return my human-readable name. */
    String getName();

    /** Return a lengthy diatribe of what I am. */
    String getDescription();

    List<Column> getColumns();

    // Iterate over all the rows, performing the given action.
    public void iterate(Consumer<Row> action);
    // TODO: get one row. Iterate with a limit.

    public Row insert(Row row);
    public Row update(Value key, Row row);
    public void delete(Value key);
}
