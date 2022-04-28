package gulik.demo;

import gulik.dolichos.ODataEntitySet;
import gulik.urad.Column;
import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import gulik.urad.queryables.collection.CollectionQueryable;

import java.util.ArrayList;
import java.util.List;

// @ODataEndpoint(namespace = "salad", container = "bowl")
public class FruitEntitySet implements ODataEntitySet {
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
        return new CollectionQueryable(this, fruit).query(q);
    }

    @Override
    public Column[] getColumns() {
        return new gulik.urad.impl.Column[] {
            new gulik.urad.impl.Column().setName("name").setType(Type.String),
            new gulik.urad.impl.Column().setName("numberOfSeeds").setType(Type.Integer)
        };
    }

    @Override
    public String getName() {
        return "Fruit";
    }

    @Override
    public Table create(Table t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public Table update(Table t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public Table delete(Table t) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }
}
