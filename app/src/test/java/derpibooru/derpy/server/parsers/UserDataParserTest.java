package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class UserDataParserTest {
    private DerpibooruUser expectedLoggedIn =
            new DerpibooruUser("CurrentUser", "https://derpicdn.net/avatars/currentUserAvatar.png")
                    .setCurrentFilter(new DerpibooruFilter(100000, "Selected Filter",
                                                           Arrays.asList(61060, 89379, 20417, 86723, 94478, 25605, 25724),
                                                           Arrays.asList(54235, 41161, 42773, 43502, 46981)));
    private DerpibooruUser expectedLoggedOut =
            new DerpibooruUser("", "https://derpicdn.net/assets/no_avatar.svg")
                    .setCurrentFilter(new DerpibooruFilter(100073, "Some Long Filter Name",
                                                           Collections.<Integer>emptyList(),
                                                           Collections.singletonList(61060)));

    private DerpibooruUser parsedLoggedIn;
    private DerpibooruUser parsedLoggedOut;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String loggedIn = loader.readTestResourceFile("SampleImageDetailedResponse.html");
        parsedLoggedIn = new UserDataParser().parseResponse(loggedIn);
        String loggedOut = loader.readTestResourceFile("SampleImageDetailedLoggedOutResponse.html");
        parsedLoggedOut = new UserDataParser().parseResponse(loggedOut);
    }

    @Test
    public void test() {
        assertThat(parsedLoggedIn, samePropertyValuesAs(expectedLoggedIn));
        assertThat(parsedLoggedIn.getCurrentFilter(), samePropertyValuesAs(expectedLoggedIn.getCurrentFilter()));
        assertThat(parsedLoggedOut, samePropertyValuesAs(expectedLoggedOut));
        assertThat(parsedLoggedOut.getCurrentFilter(), samePropertyValuesAs(expectedLoggedOut.getCurrentFilter()));
    }
}
