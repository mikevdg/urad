package gulik.urad;

import gulik.demo.Vegetable;
import gulik.demo.VegetableTable;
import gulik.urad.exceptions.ColumnDoesNotExist;
import gulik.urad.queryables.collection.CollectionQueryable;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestCollection {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private List<Vegetable> veges() {
        List<Vegetable> veges = new ArrayList<>();
        Vegetable v;

        // Put them out of order so we can try sorting them.
        v = new Vegetable();
        v.setName("cabbage");
        v.setChildrenLikeIt(false);
        v.setColour("blue");
        v.setWeight(10);
            v.setPlanted(date("2000-01-04"));
        veges.add(v);

        v = new Vegetable();
        v.setName("alfalfa");
        v.setChildrenLikeIt(true);
        v.setColour("yellow");
        v.setWeight(2);
            v.setPlanted(date("2000-01-03"));
        veges.add(v);

        v = new Vegetable();
        v.setName("brusselsprout");
        v.setChildrenLikeIt(true);
        v.setColour("grey");
        v.setWeight(5);
            v.setPlanted(date("2000-01-02"));

        veges.add(v);
        return veges;
    }

    private Date date(String d) {
        try {
            return dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOrderByName() {
        Query q = new Query();
        q.orderBy("Name");

        ResultSet result = new VegetableTable().query(q);
        QueryColumn columnNum = result.getColumnByName("Name");

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).get(columnNum).toString().equals("'alfalfa'"));
        assertTrue(v.get(1).get(columnNum).toString().equals("'brusselsprout'"));
        assertTrue(v.get(2).get(columnNum).toString().equals("'cabbage'"));
    }

    @Test
    public void testOrderByDate() {
        Query q = new Query();
        q.orderBy("Planted");

        ResultSet result = new CollectionQueryable(veges()).query(q);

        List<Row> v = result.stream().collect(Collectors.toList());
        int columnNum = result.getColumnNumber("Planted");
        assertTrue(v.get(0).get(columnNum).toString().equals("2000-01-02"));
        assertTrue(v.get(1).get(columnNum).toString().equals("2000-01-03"));
        assertTrue(v.get(2).get(columnNum).toString().equals("2000-01-04"));
    }


    @Test
    public void testOrderByWeight() {
        Query q = new Query();
        q.orderBy("Weight");

        ResultSet result = new CollectionQueryable(veges()).query(q);
        int columnNum = result.getColumnNumber("Weight");

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).get(columnNum).toString().equals("2"));
        assertTrue(v.get(1).get(columnNum).toString().equals("5"));
        assertTrue(v.get(2).get(columnNum).toString().equals("10"));
    }

    @Test
    public void testSelect() {
        Query q = new Query().select("Planted").select("Weight");
        ResultSet result = new CollectionQueryable(veges()).query(q);
        assertEquals(result.getColumnNumber("Planted"), 0);
        assertEquals(result.getColumnNumber("Weight"), 1);

        try {
            result.getColumnNumber("Name");
            fail();
        } catch (ColumnDoesNotExist e) {}
    }

    @Test
    public void testSelectAll() {
        Query q = new Query();
        ResultSet result = new CollectionQueryable(veges()).query(q);
        // We have no guarantees about the ordering of the columns.
        // Reflection on classes cannot tell us this.
        HashSet<Integer> h = new HashSet<Integer>();
        h.add(result.getColumnNumber("Name"));
        h.add(result.getColumnNumber("Colour"));
        h.add(result.getColumnNumber("Weight"));
        h.add(result.getColumnNumber("Planted"));
        assertTrue(h.size() == 4);
    }

}
