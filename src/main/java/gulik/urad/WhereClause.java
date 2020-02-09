package gulik.urad;

public class WhereClause {
    public static WhereClause equal(String columnPath, String value);
    public static WhereClause equal(String columnPath, Integer value);
    public static WhereClause like(String columnPath, String value);
    public static WhereClause notNull(String columnPath);

    /** Use this as a placeholder for a WhereClause that you removed. */
    public static WhereClause true() {}

    public static WhereClause and(WhereClause left, WhereClause right);
    public static WhereClause or(WhereClause left, WhereClause right);
    public static WhereClause not(WhereClause notThis);
}
