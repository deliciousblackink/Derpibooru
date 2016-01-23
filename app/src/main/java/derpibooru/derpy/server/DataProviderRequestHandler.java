package derpibooru.derpy.server;

public interface DataProviderRequestHandler {
    /**
     * Successfully executed Derpibooru request.
     *
     * @param result requested object
     */
    void onDataFetched(Object result);

    /**
     * Failed Derpibooru request.
     */
    void onDataRequestFailed();
}
