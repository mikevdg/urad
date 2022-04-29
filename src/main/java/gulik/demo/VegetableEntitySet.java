package gulik.demo;

import java.util.ArrayList;
import java.util.List;

import gulik.dolichos.ColumnListBuilder;
import gulik.dolichos.ODataEntitySet;
import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.queryables.collection.CollectionQueryable;
import gulik.urad.tableColumn.TableColumn;

// @ODataEndpoint(namespace = "salad", container = "bowl")
// TODO: implements Table??? extends Table? Isn't ODataEntitySet just a table?
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
        return new CollectionQueryable(this, veges).query(q);
    }

    @Override
    public List<TableColumn> getColumns() {
        return new ColumnListBuilder()
                .add("name", Type.String)
                .add("colour", Type.String) // fk("Colour", "id"))
                .add("childrenLikeIt", Type.Boolean)
                .add("weight", Type.Float)
                .pk("name")
                .build();
    }

    @Override
    public String getName() {
        return "Vegetables";
    }

    @Override
    public Table create(Table t) {
        throw new NotImplementedException();
    }

    @Override
    public Table update(Table t) {
        throw new NotImplementedException();
    }

    @Override
    public Table delete(Table t) {
        throw new NotImplementedException();
    }
}
