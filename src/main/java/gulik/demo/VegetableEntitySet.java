package gulik.demo;

import java.util.ArrayList;
import java.util.List;

import gulik.dolichos.ColumnDefinition;
import gulik.dolichos.ODataEntitySet;
import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.queryables.collection.CollectionQueryable;

// @ODataEndpoint(namespace = "salad", container = "bowl")
public class VegetableEntitySet implements ODataEntitySet {
    private List<Vegetable> veges;

    public VegetableEntitySet() {
        veges = new ArrayList<>();
        Vegetable v;

        // Put them out of order so we can try sorting them.
        v = new Vegetable();
        v.setName("cabbage");
        v.setColour("red");
        v.setChildrenLikeIt(true);
        v.setWeight(10);
        veges.add(v);

        v = new Vegetable();
        v.setName("alfalfa");
        v.setColour("white");
        v.setChildrenLikeIt(true);
        v.setWeight(1);
        veges.add(v);

        v = new Vegetable();
        v.setName("brusselsprout");
        v.setColour("green");
        v.setChildrenLikeIt(false);
        v.setWeight(5);
        veges.add(v);
    }

    // @GetEntities("Vegetable")
    public Table query(Query q) {
        return new CollectionQueryable(veges).query(q);
    }

    @Override
    public ColumnDefinition[] getColumns() {
        /*
            Ideal code: 
            import static Factory.*;
            return columns()
                .add("name", Text)
                .add("colour", fk("Colour", "id"))
                .add("childrenLikeIt", Bool)
                .add("weight", Float)
                .primaryKey("name")
                .build();
        */

        return new ColumnDefinition[] {
            new ColumnDefinition("name", Type.String),
            new ColumnDefinition("colour", Type.String),
            new ColumnDefinition("childrenLikeIt", Type.Boolean),
            new ColumnDefinition("weight", Type.Float)
        };
    }

    @Override
    public String getName() {
        return "Vegetables";
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
