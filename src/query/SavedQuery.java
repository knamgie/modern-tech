package query;

public class SavedQuery {
    private final String id_;
    private final String query_;
    private final String tableName_;

    public SavedQuery(String id, String query, String tableName) {
        id_ = id;
        query_ = query;
        tableName_ = tableName;
    }
}