package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class TagListParserTest {
    private static final DerpibooruTagDetailed expectedTagWithImplications =
            new DerpibooruTagDetailed(24257, 922, "cutelestia", "", "");
    private static final DerpibooruTagDetailed expectedTagWithSpoilerImage =
            new DerpibooruTagDetailed(36457, 165700, "oc",
                                      "Tag for any character that has not appeared in official media or merchandise; that is, a fan-made character, regardless of how popular they may be.",
                                      "https://derpicdn.net/media/W1siZiIsIjIwMTMvMTEvMjkvMTVfMDRfNTVfNjM2X29jLnBuZyJdLFsicCIsInRodW1iIiwiMjUweDI1MCJdXQ.png");
    private static final DerpibooruTagDetailed expectedCachedTag =
            new DerpibooruTagDetailed(1, 32, "test", "Tests tag caching", "https://derpicdn.net/media/something.png");

    private DerpibooruTagDetailed parsedWithImplications;
    private DerpibooruTagDetailed parsedWithSpoilerImage;
    private DerpibooruTagDetailed cached;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String tags = loader.readTestResourceFile("SampleTagListResponse.json");
        TagListParser parser = new TagListParser(Arrays.asList(expectedCachedTag));
        List<DerpibooruTagDetailed> parsed = parser.parseResponse(tags);
        parsedWithImplications = parsed.get(0);
        parsedWithSpoilerImage = parsed.get(1);
        cached = parsed.get(2);
    }

    @Test
    public void test() {
        assertThat(parsedWithImplications, samePropertyValuesAs(expectedTagWithImplications));
        assertThat(parsedWithSpoilerImage, samePropertyValuesAs(expectedTagWithSpoilerImage));
        assertThat(cached, samePropertyValuesAs(expectedCachedTag));
    }
}
