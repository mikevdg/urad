package gulik.urad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import gulik.demo.VegetableTable;
import gulik.urad.exceptions.ColumnDoesNotExist;

public class TestCollection {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date date(String d) {
        try {
            return dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOrderByName() {
        Table t = new VegetableTable();

        ResultSet result = new VegetableTable().select().orderBy("Name").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("Name").equals("'alfalfa'"));
        assertTrue(v.get(1).getByName("Name").equals("'brusselsprout'"));
        assertTrue(v.get(2).getByName("Name").equals("'cabbage'"));
    }

    @Test
    public void testOrderByDate() {
        ResultSet result = new VegetableTable().select().orderBy("Planted").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("Date").equals("2000-01-02"));
        assertTrue(v.get(1).getByName("Date").equals("2000-01-03"));
        assertTrue(v.get(2).getByName("Date").equals("2000-01-04"));
    }

    @Test
    public void testOrderByWeight() {
        Table t = new VegetableTable();
        ResultSet result = t.select().orderBy("Weight").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("Weight").equals("2"));
        assertTrue(v.get(1).getByName("Weight").equals("5"));
        assertTrue(v.get(2).getByName("Weight").equals("10"));
    }

    @Test
    public void testSelect() {
        Table t = new VegetableTable();
        ResultSet result = t.select("Planted", "Weight").fetch();
        assertEquals(result.getColumnNumber("Planted"), 0);
        assertEquals(result.getColumnNumber("Weight"), 1);

        try {
            result.getColumnNumber("Name");
            fail();
        } catch (ColumnDoesNotExist e) {
        }
    }

    @Test
    public void testSelectAll() {
        Table t = new VegetableTable();

        ResultSet result = t.select().fetch();

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
