package gulik.urad;

import gulik.urad.queryColumn.QueryColumn;
import gulik.urad.value.Value;

/** I am a row from a Table. I'm a "dumb" object; a Table is a "smart" object. All of
 * the column definitions and intelligence is in the Table implementation.
 *
 * Don't rely on me being an immutable object. It is implementation specific, but I might be re-used
 * in the next iteration.
 */
public class Row {
    private ResultSet source;
    private final Value[] values;
    private final Value[] primaryKey;

    /** Private constructor, used when you do query(). The source ResultSet is my container. */
    public Row(ResultSet source, int numColumns) {
        this.source = source;
        values = new Value[numColumns];
        for (int i=0; i<numColumns; i++) {
            values[i] = Value.NULL;
        }
        primaryKey = new Value[numColumns];
        for (int i=0; i<numColumns; i++) {
            primaryKey[i] = Value.NULL;
        }
    }

    public Value get(int columnNum) {
        return values[columnNum];
    }

    public void set(int columnNum, Value v) {
        values[columnNum] = v;
    }

    public Value getByName(String name) {
        QueryColumn column = source.getColumnByName(name);
        return get(column.getColumnIndex());
    }

    public Value getPrimaryKey(int columnNum) {
        return primaryKey[columnNum];
    }

    public void setPrimaryKey(int columnNum, Value v) {
        primaryKey[columnNum] = v;
    }
}
