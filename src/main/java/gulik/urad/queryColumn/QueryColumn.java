package gulik.urad.queryColumn;

import gulik.urad.tableColumn.TableColumn;

public class QueryColumn {
    private TableColumn origColumn;
    private String title;
    private int columnIndex;

    public int getColumnIndex() {
        return columnIndex;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public TableColumn getOrigColumn() {
        return origColumn;
    }
    public void setOrigColumn(TableColumn origColumn) {
        this.origColumn = origColumn;
    }
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public static QueryColumn from(TableColumn tc) {
        QueryColumn result = new QueryColumn();
        result.setOrigColumn(tc);
        result.setTitle(tc.getTitle());
        return result;
    }

    public String getName() {
        return origColumn.getName();
    }

    @Override
    public String toString() {
        return origColumn.getName();
    }
}
