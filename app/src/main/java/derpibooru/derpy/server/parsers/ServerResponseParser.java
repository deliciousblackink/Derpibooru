package derpibooru.derpy.server.parsers;

/**
 * An interface for Derpibooru response parsers.
 */
public interface ServerResponseParser<T> {
    /**
     * Parses the server response.
     *
     * @param rawResponse server response string
     * @return parsed object
     */
    T parseResponse(String rawResponse) throws Exception;
}
