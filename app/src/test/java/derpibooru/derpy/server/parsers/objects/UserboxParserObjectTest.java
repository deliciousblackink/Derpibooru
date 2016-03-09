package derpibooru.derpy.server.parsers.objects;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserboxParserObjectTest {
    private UserboxParserObject loggedInBox;
    private UserboxParserObject loggedOutBox;

    @Before
    public void setUp() {
        TestResourceLoader loader = new TestResourceLoader();
        String loggedIn = loader.readTestResourceFile("UserboxLoggedIn.html");
        loggedInBox = new UserboxParserObject(loggedIn);
        String loggedOut = loader.readTestResourceFile("UserboxLoggedOut.html");
        loggedOutBox = new UserboxParserObject(loggedOut);
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
