package gulik.urad.queryables.collection;

import gulik.urad.Column;
import gulik.urad.NotImplemented;
import gulik.urad.Row;
import gulik.urad.Table;
import gulik.urad.value.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** I'm returned if a query is performed with no data present - meaning that we can't derive columns. */
public class EmptyTable implements Table {
    final private String name;

    public EmptyTable(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public List<Column> getColumns() {
        return new ArrayList<>();
    }

    @Override
    public List<Column> getPrimaryKey() {
        return new ArrayList<>();
    }

    @Override
    public Row insert(Row row) {
        throw new NotImplemented();
    }

    @Override
    public Row update(Value key, Row row) {
        throw new NotImplemented();
    }

    @Override
    public void delete(Value key) {
        throw new NotImplemented();

    }

    @Override
    public Iterator<Row> iterator() {
        throw new NotImplemented();
    }
}
