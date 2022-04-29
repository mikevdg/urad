package gulik.urad.tableColumn;

import gulik.urad.Type;

public class TableColumn {
    private String name;
    private String title;
    private Type type;
    private boolean isPrimaryKey;
    private int position;

    public String getName() {
        return name;
    }

    public TableColumn setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TableColumn setTitle(String title) {
        this.title = title;
        return this;
    }

    public Type getType() {
        return type;
    }

    public TableColumn setType(Type type) {
        this.type = type;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public TableColumn setPosition(int position) {
        this.position = position;
        return this;
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public void setPrimaryKey(boolean yesno) {
        this.isPrimaryKey = yesno;
    }
}
