package derpibooru.derpy.server;

/**
 * An interface for Derpibooru response parsers.
 */
interface ServerResponseParser {
    /**
     * Parses the server response.
     *
     * @param rawResponse server response string
     * @return parsed object
     */
    Object parseResponse(String rawResponse) throws Exception;
}
