package gulik.demo;

import java.util.LinkedList;
import java.util.List;

import gulik.dolichos.ODataEntitySet;
import gulik.dolichos.ODataServlet;

public class VegetableServlet extends ODataServlet {

    @Override
    protected List<ODataEntitySet> getEntitySets() {
        List<ODataEntitySet> result = new LinkedList<ODataEntitySet>();
        result.add(new VegetableEntitySet());
        result.add(new FruitEntitySet());
        return result;
    }
    
}
