package gulik.demo;

import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import gulik.urad.queryables.collection.CollectionQueryable;

import java.util.ArrayList;
import java.util.List;

@ODataEndpoint(namespace = "salad", container = "bowl")
public class VegetableEndpoint {
    private List<Vegetable> veges;

    public VegetableEndpoint() {
        veges = new ArrayList<>();
        Vegetable v;

        // Put them out of order so we can try sorting them.
        v = new Vegetable();
        v.setName("cabbage");
        veges.add(v);

        v = new Vegetable();
        v.setName("alfalfa");
        veges.add(v);

        v = new Vegetable();
        v.setName("brusselsprout");
        veges.add(v);


    }

    @GetEntities("Vegetable")
    public Table getVegetables(Query q) {
        return new CollectionQueryable(veges).query(q);
    }
}
