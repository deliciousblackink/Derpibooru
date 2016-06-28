package derpibooru.derpy.server.parsers;

import com.google.common.collect.Lists;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class ImageListParserTest {
    private static final DerpibooruImageThumb firstImageThumb =
            new DerpibooruImageThumb(960596, 3, 23, 1, 4,
                                     "https://derpicdn.net/img/2015/8/18/960596/thumb.png",
                                     "https://derpicdn.net/img/2015/8/18/960596/large.png",
                                     "https://derpicdn.net/dummy_spoiler",
                                     EnumSet.of(DerpibooruImageInteraction.InteractionType.Downvote,
                                                DerpibooruImageInteraction.InteractionType.Fave));
    private static final DerpibooruTagDetailed dummySpoilerTag =
            new DerpibooruTagDetailed(67509, 0, "artist:pikapetey", "", "", "https://derpicdn.net/dummy_spoiler");
    private static final ArrayList<DerpibooruTagDetailed> dummyFilter = Lists.newArrayList(dummySpoilerTag);

    private List<DerpibooruImageThumb> parsed;

    @Before
    public void setUp() throws JSONException {
        TestResourceLoader loader = new TestResourceLoader();
        String list = loader.readTestResourceFile("SampleImageListResponse.json");
        ImageListParser parser = new ImageListParser(dummyFilter);
        parsed = parser.parseResponse(list);
    }

    @Test
    public void run() throws Exception {
        assertThat("The number of items parsed does not match the input provided",
                   parsed.size(), is(15));
        assertThat(parsed.get(0), samePropertyValuesAs(firstImageThumb));
    }
}
