package gulik.urad.queryables.collection;

import gulik.urad.Row;

import java.util.Iterator;

public class CollectionIterator implements Iterator<Row> {
    private final RowGenerator rowGenerator;
    private Iterator<?> subIterator;

    public CollectionIterator(RowGenerator rg) {
        this.rowGenerator = rg;
        subIterator = rg.getSource().iterator();
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
