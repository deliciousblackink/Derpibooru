package derpibooru.derpy.server.parsers;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class ImageDetailedParserTest {
    private static final DerpibooruImageThumb thumb =
            new DerpibooruImageThumb(655777, 607986, 240, 3, 154, 32,
                                     "https://derpicdn.net/img/2014/6/17/655777/thumb.gif",
                                     "https://derpicdn.net/img/2014/6/17/655777/large.gif", "",
                                     EnumSet.of(DerpibooruImageInteraction.InteractionType.Upvote,
                                                DerpibooruImageInteraction.InteractionType.Fave));
    private static final List<DerpibooruTag> tags = Arrays.asList(new DerpibooruTag(54235, 42181, "animated"),
                                                                  new DerpibooruTag(26668, 3, "excalibur (1981)"));
    private static final DerpibooruImageDetailed expected =
            new DerpibooruImageDetailed(thumb, "",
                                        "https://derpicdn.net/img/view/2014/6/17/655777__safe_animated_upvotes+galore_trixie_frown_wide+eyes_car_soldier_thought+bubble_wheel.gif",
                                        "Background Pony #14C8", "Trixie has no one to blame but herself.", "2014-06-17T22:32:45Z",
                                        tags, Arrays.asList("User1", "User2", "User3"));

    private DerpibooruImageDetailed parsed;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String list = loader.readTestResourceFile("SampleImageDetailedResponse.html");
        ImageDetailedParser parser = new ImageDetailedParser();
        parsed = parser.parseResponse(list);
    }

    @Test
    public void testThumb() {
        assertThat(parsed.getThumb(), samePropertyValuesAs(expected.getThumb()));
    }

    @Test
    public void testDetailedFields() {
        assertThat(parsed.getSourceUrl(), is(expected.getSourceUrl()));
        assertThat(parsed.getDownloadUrl(), is(expected.getDownloadUrl()));
        assertThat(parsed.getUploader(), is(expected.getUploader()));
        assertThat(parsed.getDescription(), is(expected.getDescription()));
        assertThat(parsed.getCreatedAt(), is(expected.getCreatedAt()));
    }

    @Test
    public void testTags() {
        assertThat(parsed.getTags().size(), is(expected.getTags().size()));
        /* tags are required to be sorted the way they are present on the Derpibooru page (alphabetically);
         * make sure that expected tags are ordered the right way */
        for (int iterator = 0; iterator < parsed.getTags().size(); iterator++) {
            assertThat(parsed.getTags().get(iterator), samePropertyValuesAs(expected.getTags().get(iterator)));
        }
    }

    @Test
    public void testFavedBy() {
        assertThat(parsed.getFavedBy(), is(expected.getFavedBy()));
    }
}
