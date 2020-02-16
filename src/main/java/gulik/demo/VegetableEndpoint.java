package gulik.demo;

import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;

@ODataEndpoint
public class VegetableEndpoint {
    @GetEntities("Vegetable")
    public Table getVegetables(Query q) {
        /* TODO: I should be:
        return new SomethingQueryable(q);
         */
        return null;
    }
}
