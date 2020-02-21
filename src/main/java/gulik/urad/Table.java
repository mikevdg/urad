package gulik.urad;

import gulik.urad.value.Value;

import java.util.List;

/**
 * I am tabular data which is the result of a query.
 */
public interface Table extends Iterable<gulik.urad.Row> {
    /** Return my SQL name */
    String getCode();

    /** Return my human-readable name. */
    String getName();

    /** Return a lengthy diatribe of what I am. */
    String getDescription();

    List<Column> getColumns();
    List<Column> getPrimaryKey();

    // Maybe support stream()?

    public Row insert(Row row);
    public Row update(Value key, Row row);
    public void delete(Value key);
}
