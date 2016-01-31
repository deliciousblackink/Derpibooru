package derpibooru.derpy.server;

public interface ProviderRequestHandler {
    /**
     * Successfully executed Derpibooru request.
     *
     * @param result requested object
     */
    void onRequestCompleted(Object result);

    /**
     * Failed Derpibooru request.
     */
    void onRequestFailed();
}
