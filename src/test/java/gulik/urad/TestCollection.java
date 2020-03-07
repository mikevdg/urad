package gulik.urad;

import gulik.demo.Vegetable;
import gulik.urad.queryables.collection.CollectionQueryable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class TestCollection {

    private List<Vegetable> veges() {
        List<Vegetable> veges = new ArrayList<>();
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
        return veges;
    }

    @Test
    public void testOrderBy() {
        Query q = new Query();
        q.orderBy("Name");

        Table result = new CollectionQueryable(veges()).query(q);

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).get(0).equals("alfalfa"));
        assertTrue(v.get(1).get(0).equals("alfalfa"));
        assertTrue(v.get(2).get(0).equals("alfalfa"));
    }
}
