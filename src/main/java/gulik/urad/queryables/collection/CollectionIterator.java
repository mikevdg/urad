package gulik.urad.queryables.collection;

import java.util.Iterator;

import gulik.urad.Row;

public class CollectionIterator implements Iterator<Row> {
    private final RowGenerator rowGenerator;
    private Iterator<?> subIterator;

    public CollectionIterator(RowGenerator rg) {
        this.rowGenerator = rg;
        subIterator = rg.sourceIterator();
    }

    @Override
    public boolean hasNext() {
        return subIterator.hasNext();
    }

    @Override
    public Row next() {
        return rowGenerator.toRow(subIterator.next());
    }

}
