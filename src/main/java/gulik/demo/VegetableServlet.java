package gulik.demo;

import java.util.LinkedList;
import java.util.List;

import gulik.dolichos.ODataEntity;
import gulik.dolichos.ODataServlet;

public class VegetableServlet extends ODataServlet {

    @Override
    protected List<ODataEntity> getEntities() {
        List<ODataEntity> result = new LinkedList<ODataEntity>();
        result.add(new VegetableEntitySet());
        result.add(new FruitEntitySet());
        return result;
    }
    
}
