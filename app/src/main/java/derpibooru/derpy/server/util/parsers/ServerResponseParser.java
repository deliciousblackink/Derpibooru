package derpibooru.derpy.server.util.parsers;

/**
 * An interface for Derpibooru response parsers.
 */
public interface ServerResponseParser {
    /**
     * Parses the server response.
     *
     * @param rawResponse server response string
     * @return parsed object
     */
    Object parseResponse(String rawResponse) throws Exception;
}
