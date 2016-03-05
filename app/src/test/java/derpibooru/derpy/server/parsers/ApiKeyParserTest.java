package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiKeyParserTest {
    private String expected = "Tr0ub4dor&3"; /* correcthorsebatterystaple */

    private String parsed;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String key = loader.readTestResourceFile("SampleApiKeyResponse.html");
        ApiKeyParser parser = new ApiKeyParser();
        parsed = parser.parseResponse(key);
    }

    @Test
    public void test() {
        assertThat(parsed, is(expected));
    }
}
