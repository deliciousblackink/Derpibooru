package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruFilter;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class FilterListParserTest {
    private static final DerpibooruFilter expectedSystemFilter =
            new DerpibooruFilter(100073, "Default", Arrays.asList(61060, 89379), Arrays.asList(41133, 41161),
                                 "The site's default filter. Hides \"not art\" content, and should hide images that would exceed the show's own rating.",
                                 Arrays.asList("1000 hours in ms paint", "background pony strikes again"),
                                 Arrays.asList("seizure warning", "semi-grimdark"), true, 114000);
    private static final DerpibooruFilter expectedUserFilter =
            new DerpibooruFilter(12, "User-Defined Filter", Arrays.asList(61060, 89379), Arrays.asList(41133, 41161),
                                 "For those few that may have liked it.",
                                 Arrays.asList("1000 hours in ms paint", "background pony strikes again"),
                                 Arrays.asList("seizure warning", "semi-grimdark"), false, 1);

    private DerpibooruFilter parsedSystem;
    private DerpibooruFilter parsedUser;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String filters = loader.readTestResourceFile("SampleFilterListResponse.json");
        FilterListParser parser = new FilterListParser();
        parsedSystem = parser.parseResponse(filters).get(0);
        parsedUser = parser.parseResponse(filters).get(1);
    }

    @Test
    public void test() {
        assertThat(parsedSystem, samePropertyValuesAs(expectedSystemFilter));
        assertThat(parsedUser, samePropertyValuesAs(expectedUserFilter));
    }
}
