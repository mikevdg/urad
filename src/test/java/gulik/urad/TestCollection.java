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


    @Test
    public void testOrderByName() {
        Table t = new VegetableTable();

        ResultSet result = new VegetableTable().select().orderBy("name").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("name").equals("'alfalfa'"));
        assertTrue(v.get(1).getByName("name").equals("'brusselsprout'"));
        assertTrue(v.get(2).getByName("name").equals("'cabbage'"));
    }

    @Test
    public void testOrderByDate() {
        ResultSet result = new VegetableTable().select().orderBy("planted").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("planted").toString().equals("2000-01-02"));
        assertTrue(v.get(1).getByName("planted").toString().equals("2000-01-03"));
        assertTrue(v.get(2).getByName("planted").toString().equals("2000-01-04"));
    }

    @Test
    public void testOrderByWeight() {
        Table t = new VegetableTable();
        ResultSet result = t.select().orderBy("weight").fetch();

        List<Row> v = result.stream().collect(Collectors.toList());
        assertTrue(v.get(0).getByName("weight").equals("2"));
        assertTrue(v.get(1).getByName("weight").equals("5"));
        assertTrue(v.get(2).getByName("weight").equals("10"));
    }

    @Test
    public void testSelect() {
        Table t = new VegetableTable();
        ResultSet result = t.select("planted", "weight").fetch();
        assertEquals(result.getColumnNumber("planted"), 0);
        assertEquals(result.getColumnNumber("weight"), 1);

        try {
            result.getColumnNumber("name");
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
        h.add(result.getColumnNumber("name"));
        h.add(result.getColumnNumber("colour"));
        h.add(result.getColumnNumber("childrenLikeIt"));
        h.add(result.getColumnNumber("weight"));
        assertTrue(h.size() == 4);
    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Date date(String d) {
        try {
            return dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
