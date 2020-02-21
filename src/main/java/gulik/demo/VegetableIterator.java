package gulik.demo;

import gulik.urad.impl.Row;
import gulik.urad.value.StringValue;

import java.util.Iterator;

/**
 * Your code will not look like this. I am a disposable class for testing.
 */
public class VegetableIterator implements Iterator<gulik.urad.Row> {

    int next = 3;

    @Override
    public boolean hasNext() {
        return next > 0;
    }

    @Override
    public gulik.urad.Row next() {
        next--;
        Row result = new gulik.urad.impl.Row(1);
        result.set(0, new StringValue("carrot"));
        return result;
    }
}
