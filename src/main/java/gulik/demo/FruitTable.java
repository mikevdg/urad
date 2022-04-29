package gulik.demo;

import gulik.dolichos.ColumnListBuilder;
import gulik.urad.Query;
import gulik.urad.ResultSet;
import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import gulik.urad.queryables.collection.CollectionQueryable;
import gulik.urad.tableColumn.TableColumn;

import java.util.ArrayList;
import java.util.List;

// @ODataEndpoint(namespace = "salad", container = "bowl")
public class FruitTable implements Table {
    private List<Fruit> fruit;

    public FruitTable() {
        this.fruit = new ArrayList<>();
        Fruit v;

        v = new Fruit();
        v.setName("dragonfruit");
        v.setNumberOfSeeds(5);
        fruit.add(v);

        v = new Fruit();
        v.setName("apple");
        v.setNumberOfSeeds(5);
        fruit.add(v);
    }

    // @GetEntities("Vegetable")
    public ResultSet query(Query q) {
        return new CollectionQueryable(this, fruit).query(q);
    }

    @Override
    public List<TableColumn> getColumns() {
        return new ColumnListBuilder()
            .add("name", Type.String)
            .add("numberOfSeeds", Type.Integer)
            .pk("name")
            .build();
    }

    @Override
    public String getName() {
        return "Fruit";
    }

    @Override
    public ResultSet create(ResultSet t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public ResultSet update(ResultSet t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public ResultSet delete(ResultSet t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }
}
