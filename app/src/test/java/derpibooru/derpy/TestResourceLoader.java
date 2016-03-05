package derpibooru.derpy;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertNotNull;

public class TestResourceLoader {
    public String readTestResourceFile(String file) {
        String pathToResource = String.format("/resources/%s", file);
        final InputStream resourceAsStream = getClass().getResourceAsStream(pathToResource);

        assertNotNull("Failed to load test resource file. Make sure the 'copyResDirectoryToClasses' Gradle task has been executed prior to running tests.",
                      resourceAsStream);

        String response;
        try {
            InputStreamReader reader = new InputStreamReader(resourceAsStream);
            response = CharStreams.toString(reader);
            resourceAsStream.close();
        } catch (IOException e) {
            return null;
        }
        return response;
    }
}
