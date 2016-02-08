package derpibooru.derpy.server;

import com.google.common.io.CharStreams;

import java.io.InputStream;
import java.io.InputStreamReader;

import derpibooru.derpy.server.parsers.ServerResponseParser;

public class ResponseParserTest {
    private ServerResponseParser mParser;

    public ResponseParserTest(ServerResponseParser parserToTest) {
        mParser = parserToTest;
    }

    protected Object runParserWithInputResource(String pathToResource) throws Exception {
        final InputStream resourceAsStream =
                this.getClass().getResourceAsStream(pathToResource);
        InputStreamReader r = new InputStreamReader(resourceAsStream);
        String response = CharStreams.toString(r);
        resourceAsStream.close();
        return mParser.parseResponse(response);
    }
}
