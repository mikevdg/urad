package gulik.demo;

import java.util.LinkedList;
import java.util.List;

import gulik.dolichos.ODataServlet;
import gulik.urad.Table;

public class VegetableServlet extends ODataServlet {

    @Override
    protected List<Table> getEntitySets() {
        List<Table> result = new LinkedList<Table>();
        result.add(new VegetableTable());
        result.add(new FruitTable());
        return result;
    }
    
}
