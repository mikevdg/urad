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
public class VegetableEntitySet implements ODataEntity {
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
                .column("name", Text)
                .column("colour", fk("Colour", "id"))
                .column("childrenLikeIt", Bool)
                .column("weight", Float)
                .build();
        */

        // TODO: column types.
        return new ColumnDefinition[] {
            new ColumnDefinition("name"),
            new ColumnDefinition("colour"),
            new ColumnDefinition("childrenLikeIt"),
            new ColumnDefinition("weight")
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
