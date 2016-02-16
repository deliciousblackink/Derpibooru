package derpibooru.derpy.server;

public interface QueryHandler {
    /**
     * Successfully executed Derpibooru query.
     *
     * @param result requested object
     */
    void onQueryExecuted(Object result);

    /**
     * Failed Derpibooru query.
     */
    void onQueryFailed();
}
