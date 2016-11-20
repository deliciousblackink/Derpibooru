package derpibooru.derpy.server.parsers.objects;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.server.parsers.UserDataParser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HeaderParserObjectTest {
    private HeaderParserObject loggedInBox;
    private HeaderParserObject loggedOutBox;

    @Before
    public void setUp() {
        TestResourceLoader loader = new TestResourceLoader();
        String loggedIn = loader.readTestResourceFile("HeaderLoggedIn.html");
        loggedInBox = new HeaderParserObject(Jsoup.parse(loggedIn));
        String loggedOut = loader.readTestResourceFile("HeaderLoggedOut.html");
        loggedOutBox = new HeaderParserObject(Jsoup.parse(loggedOut));
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

    @Test
    public void testAvatarUrl() {
        assertThat(loggedInBox.getAvatarUrl(), is("https://derpicdn.net/avatars/muh_awesome_user.png"));
        assertThat(loggedOutBox.getAvatarUrl(), is(UserDataParser.DEFAULT_AVATAR_SRC));
    }
}
