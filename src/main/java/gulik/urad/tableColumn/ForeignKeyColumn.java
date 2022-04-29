package gulik.urad.tableColumn;

import gulik.urad.ResultSet;

public class ForeignKeyColumn extends TableColumn {
    private TableColumn keyColumn;    
    private ResultSet foreignTable;
    private TableColumn foreignColumn;
}
