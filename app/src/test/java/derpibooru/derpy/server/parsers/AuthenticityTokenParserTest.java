package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthenticityTokenParserTest {
    private String expected = "tokenAdfea223Q+Qrt34ta==";

    private String parsed;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String tokenPage = loader.readTestResourceFile("SampleImageDetailedResponse.html");
        AuthenticityTokenParser parser = new AuthenticityTokenParser();
        parsed = parser.parseResponse(tokenPage);
    }

    @Test
    public void test() {
        assertThat(parsed, is(expected));
    }
}
