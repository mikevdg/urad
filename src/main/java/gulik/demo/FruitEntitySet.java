package gulik.demo;

import gulik.dolichos.ColumnDefinition;
import gulik.dolichos.ODataEntity;
import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import gulik.urad.queryables.collection.CollectionQueryable;

import java.util.ArrayList;
import java.util.List;

// @ODataEndpoint(namespace = "salad", container = "bowl")
public class FruitEntitySet implements ODataEntity {
    private List<Fruit> fruit;

    public FruitEntitySet() {
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
    public Table query(Query q) {
        return new CollectionQueryable(fruit).query(q);
    }

    @Override
    public ColumnDefinition[] getColumns() {
        // TODO: column types.
        return new ColumnDefinition[] {
            new ColumnDefinition("name"),
            new ColumnDefinition("numberOfSeeds"),
        };
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Table create(Table t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Table update(Table t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Table delete(Table t) {
        // TODO Auto-generated method stub
        return null;
    }
}
