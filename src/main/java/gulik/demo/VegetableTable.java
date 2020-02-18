package gulik.demo;

import gulik.urad.Column;
import gulik.urad.Table;
import gulik.urad.TableIterator;
import gulik.urad.impl.Row;
import gulik.urad.value.Value;

import java.util.ArrayList;
import java.util.List;

/** TODO: delete me and dynamically generate tables. */
public class VegetableTable implements Table {
    @Override
    public String getCode() {
        return "Vegetable";
    }

    @Override
    public String getName() {
        return "Vegetable";
    }

    @Override
    public String getDescription() {
        return "Vegetable";
    }

    @Override
    public List<Column> getColumns() {
        List<Column> result = new ArrayList<>();
        result.add(new VegetableNameColumn());
        return result;
    }

    @Override
    public List<Column> getPrimaryKey() {
        return getColumns(); // I've only got one column!
    }

    @Override
    public TableIterator iterator() {
        return new VegetableIterator();
    }

    @Override
    public Row insert(Row row) {
        return null;
    }

    @Override
    public Row update(Value key, Row row) {
        return null;
    }

    @Override
    public void delete(Value key) {

    }
}
