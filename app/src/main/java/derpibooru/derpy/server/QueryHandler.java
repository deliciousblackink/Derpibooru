package derpibooru.derpy.server;

public interface QueryHandler<T> {
    /**
     * Successfully executed Derpibooru query.
     *
     * @param result requested object
     */
    void onQueryExecuted(T result);

    /**
     * Failed Derpibooru query.
     */
    void onQueryFailed();
}
