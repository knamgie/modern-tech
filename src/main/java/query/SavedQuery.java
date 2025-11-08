package query;

public record SavedQuery(String id_, String query_, String tableName_) {
    public void showInfo() {
        IO.println("table: `" + tableName_ + "` | id: `" + id_ + "` | query: `" + query_ + "`");
    }
}