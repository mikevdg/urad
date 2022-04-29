package gulik.urad.tableColumn;

import gulik.urad.Table;

public class ForeignKeyColumn extends TableColumn {
    private TableColumn keyColumn;    
    private Table foreignTable;
    private TableColumn foreignColumn;
}
