package gulik.demo;

import gulik.urad.TableIterator;
import gulik.urad.impl.Row;
import gulik.urad.value.StringValue;

import java.util.function.Consumer;

public class VegetableIterator implements TableIterator {
    @Override
    public void iterate(Consumer<Row> action) {
        Row result = new gulik.urad.impl.Row(1);
        result.set(0, new StringValue("carrot"));
        action.accept(result);
    }
}
