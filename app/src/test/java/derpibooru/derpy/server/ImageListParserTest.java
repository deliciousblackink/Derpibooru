package derpibooru.derpy.server;

import com.google.common.collect.Lists;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.ImageListParser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImageListParserTest {
    private static final DerpibooruImageThumb firstImageThumb =
            new DerpibooruImageThumb(960596, 960387, 3, 23, 1, 4,
                                     "https://derpicdn.net/img/2015/8/18/960596/thumb.png",
                                     "https://derpicdn.net/img/2015/8/18/960596/large.png",
                                     "https://derpicdn.net/dummy_spoiler",
                                     EnumSet.of(DerpibooruImageInteraction.InteractionType.Downvote,
                                                DerpibooruImageInteraction.InteractionType.Fave));
    private static final DerpibooruTagDetailed dummySpoilerTag =
            new DerpibooruTagDetailed(67509, 0, "artist:pikapetey", "", "//derpicdn.net/dummy_spoiler");
    private static final ArrayList<DerpibooruTagDetailed> dummyFilter = Lists.newArrayList(dummySpoilerTag);

    private List<DerpibooruImageThumb> mParsedList;

    @Before
    public void setUp() throws JSONException {
        TestResourceLoader loader = new TestResourceLoader();
        String list = loader.readTestResourceFile("SampleImageListResponse.json");
        ImageListParser parser = new ImageListParser(dummyFilter);
        mParsedList = parser.parseResponse(list);
    }

    @Test
    public void run() throws Exception {
        assertThat("The number of items parsed does not match the input provided",
                   mParsedList.size(), is(15));
        compareImageThumb(firstImageThumb, mParsedList.get(0));
    }

    private void compareImageThumb(DerpibooruImageThumb expected, DerpibooruImageThumb parsed) {
        assertThat(parsed.getId(), is(expected.getId()));
        assertThat(parsed.getIdForImageInteractions(), is(expected.getIdForImageInteractions()));
        assertThat(parsed.getUpvotes(), is(expected.getUpvotes()));
        assertThat(parsed.getDownvotes(), is(expected.getDownvotes()));
        assertThat(parsed.getFaves(), is(expected.getFaves()));
        assertThat(parsed.getCommentCount(), is(expected.getCommentCount()));
        assertThat(parsed.getThumbUrl(), is(expected.getThumbUrl()));
        assertThat(parsed.getLargeImageUrl(), is(expected.getLargeImageUrl()));
        assertThat(parsed.getSpoilerImageUrl(), is(expected.getSpoilerImageUrl()));

        assertThat(parsed.getImageInteractions(), is(expected.getImageInteractions()));
    }
}
