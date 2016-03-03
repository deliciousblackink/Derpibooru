package derpibooru.derpy.server;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
                                     "https://derpicdn.net/dummy_spoiler");
    private static final DerpibooruTagDetailed dummySpoilerTag =
            new DerpibooruTagDetailed(67509, 0, "artist:pikapetey", "", "//derpicdn.net/dummy_spoiler");
    private static final ArrayList<DerpibooruTagDetailed> dummyFilter = Lists.newArrayList(dummySpoilerTag);
    private ResponseParserTest mTest;

    @Before
    public void setUp() {
        mTest = new ResponseParserTest(new ImageListParser(dummyFilter));
    }

    @Test
    public void run() throws Exception {
        Object result =
                mTest.runParserWithInputResource("/resources/SampleImageListResponse.json");
        ArrayList<DerpibooruImageThumb> images = (ArrayList<DerpibooruImageThumb>) result;
        assertThat("The number of items parsed does not match the input provided",
                   images.size(), is(15));
        compareImageThumb(firstImageThumb, images.get(0));
    }

    private void compareImageThumb(DerpibooruImageThumb expected, DerpibooruImageThumb parsed) {
        assertThat("Id does not match", parsed.getId(), is(expected.getId()));
        assertThat("Internal id does not match", parsed.getIdForImageInteractions(), is(expected.getIdForImageInteractions()));
        assertThat("Upvotes do not match", parsed.getUpvotes(), is(expected.getUpvotes()));
        assertThat("Downvotes do not match", parsed.getDownvotes(), is(expected.getDownvotes()));
        assertThat("Faves do not match", parsed.getFaves(), is(expected.getFaves()));
        assertThat("Number of comments does not match", parsed.getCommentCount(), is(expected.getCommentCount()));
        assertThat("Thumb url does not match", parsed.getThumbUrl(), is(expected.getThumbUrl()));
        assertThat("Full image url does not match", parsed.getLargeImageUrl(), is(expected.getLargeImageUrl()));
        assertThat("Spoiler image does not match", parsed.getSpoilerImageUrl(), is(expected.getSpoilerImageUrl()));
    }
}
