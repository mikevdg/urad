package gulik.urad.queryables.collection;

import gulik.urad.Row;

import java.util.Collection;

public interface RowGenerator {
    Collection<?> getSource();

    Row toRow(Object something);
}
