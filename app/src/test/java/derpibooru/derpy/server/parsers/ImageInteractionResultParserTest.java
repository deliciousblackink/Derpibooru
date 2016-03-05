package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;

public class ImageInteractionResultParserTest {
    private static final DerpibooruImageInteraction expected =
            new DerpibooruImageInteraction(2643, 2005, 2778, 135, 100035, DerpibooruImageInteraction.InteractionType.Upvote);

    private DerpibooruImageInteraction parsed;

    @Before
    public void setUp() throws JSONException {
        TestResourceLoader loader = new TestResourceLoader();
        String interaction = loader.readTestResourceFile("SampleImageInteractionResponse.json");
        ImageInteractionResultParser parser =
                new ImageInteractionResultParser(100035, DerpibooruImageInteraction.InteractionType.Upvote);
        parsed = parser.parseResponse(interaction);
    }

    @Test
    public void test() {
        assertThat(parsed, samePropertyValuesAs(expected));
    }
}
