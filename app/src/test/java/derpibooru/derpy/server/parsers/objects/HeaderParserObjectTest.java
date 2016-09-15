package derpibooru.derpy.server.parsers.objects;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HeaderParserObjectTest {
    private HeaderParserObject loggedInBox;
    private HeaderParserObject loggedOutBox;

    @Before
    public void setUp() {
        TestResourceLoader loader = new TestResourceLoader();
        String loggedIn = loader.readTestResourceFile("HeaderLoggedIn.html");
        loggedInBox = new HeaderParserObject(loggedIn);
        String loggedOut = loader.readTestResourceFile("HeaderLoggedOut.html");
        loggedOutBox = new HeaderParserObject(loggedOut);
    }

    @Test
    public void testIsLoggedIn() {
        assertThat(loggedInBox.isLoggedIn(), is(true));
        assertThat(loggedOutBox.isLoggedIn(), is(false));
    }

    @Test
    public void testFilterName() {
        assertThat(loggedInBox.getFilterName(), is("Some Long Filter Name"));
        assertThat(loggedOutBox.getFilterName(), is("Some Long Filter Name"));
    }
}
