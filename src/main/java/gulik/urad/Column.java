package gulik.urad;

public interface Column {
    public String getName();
    public String getTitle();
    public Type getType();

    public boolean isPrimaryKey();
    public int getPosition();
}
