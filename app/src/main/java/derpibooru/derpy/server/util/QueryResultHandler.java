package derpibooru.derpy.server.util;

/**
 * An interface for asynchronous server requests.
 */
public interface QueryResultHandler {
    /**
     * Successfully executed Derpibooru request.
     *
     * @param result requested object
     */
    void onQueryExecuted(Object result);

    /**
     * Failed Derpibooru request.
     */
    void onQueryFailed();
    /* TODO: pass the exception object via 'onQueryFailed' */
}
