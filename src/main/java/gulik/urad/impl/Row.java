package gulik.urad.impl;

import gulik.urad.value.Value;

/** I'm basically just an array. All of my important information, such as how many columns I have
 * and what type they are are in my owning Table object.
 *
 */
public class Row implements gulik.urad.Row{
    Value[] values;

    public Row(int numColumns) {
        values = new Value[numColumns];
        for (int i=0; i<numColumns; i++) {
            values[i] = Value.NULL;
        }
    }

    public Value get(int columnNum) {
        return values[columnNum];
    }

    public void set(int columnNum, Value v) {
        values[columnNum] = v;
    }
}
