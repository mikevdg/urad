package gulik.urad;

import gulik.urad.impl.Row;
import gulik.urad.value.Value;

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
    List<Column> getPrimaryKey();

    TableIterator iterator(); // We aren't using the standard Java ones because they suck.
    // Maybe support stream()?

    public Row insert(Row row);
    public Row update(Value key, Row row);
    public void delete(Value key);
}
