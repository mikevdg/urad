package gulik.urad.impl;

import java.util.List;

import gulik.demo.NotImplementedException;
import gulik.urad.Query;
import gulik.urad.ResultSet;
import gulik.urad.Table;
import gulik.urad.tableColumn.TableColumn;

public abstract class AbstractTable implements Table {
    public abstract String getName();
    public abstract List<TableColumn> getColumns();
    
    public Query select() {
        return new Query(this);
    }
    
    public Query select(String column1) {
        return new Query(this).select(column1);
    }

    public Query select(String column1, String column2) {
        return new Query(this).select(column1).select(column2);
    }

    public Query select(String column1, String column2, String column3) {
        return new Query(this).select(column1).select(column2).select(column3);
    }

    // TODO
    public ResultSet create(ResultSet t) {
        throw new NotImplementedException();
    };
    
    // TODO
    public ResultSet update(ResultSet t) {
        throw new NotImplementedException();
    };

    // TODO
    public ResultSet delete(ResultSet t){
        throw new NotImplementedException();
    } ;
}
