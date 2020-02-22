package gulik.demo;

import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import gulik.urad.queryables.collection.CollectionQueryable;

import java.util.ArrayList;
import java.util.List;

@ODataEndpoint
public class VegetableEndpoint {
    private List<Vegetable> veges;

    public VegetableEndpoint() {
        veges = new ArrayList<>();
        Vegetable v;

        v = new Vegetable();
        v.setName("alfalfa");
        veges.add(v);

        v = new Vegetable();
        v.setName("brussel sprout");
        veges.add(v);

        v = new Vegetable();
        v.setName("cabbage");
        veges.add(v);

    }

    @GetEntities("Vegetable")
    public Table getVegetables(Query q) {
        return new CollectionQueryable(veges).query(q);
    }
}
