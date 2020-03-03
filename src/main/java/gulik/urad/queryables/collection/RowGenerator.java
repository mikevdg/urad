package gulik.urad.queryables.collection;

import gulik.urad.Row;

import java.util.Collection;
import java.util.Iterator;

public interface RowGenerator {
    Iterator sourceIterator();

    Row toRow(Object something);
}
