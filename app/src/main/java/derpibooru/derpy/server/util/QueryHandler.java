package derpibooru.derpy.server.util;

/**
 * An interface for asynchronous server requests.
 */
public interface QueryHandler {
    /**
     * Successfully performed Derpibooru request.
     *
     * @param result requested object
     */
    void queryPerformed(Object result);

    /**
     * Failed Derpibooru request.
     */
    void queryFailed();
    /* TODO: pass the exception object via 'queryFailed' */
}
