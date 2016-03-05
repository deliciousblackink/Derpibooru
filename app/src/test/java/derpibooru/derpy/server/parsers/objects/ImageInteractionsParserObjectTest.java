package derpibooru.derpy.server.parsers.objects;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImageInteractionsParserObjectTest {
    private ImageInteractionsParserObject interactions;

    @Before
    public void setUp() throws JSONException {
        TestResourceLoader loader = new TestResourceLoader();
        String res = loader.readTestResourceFile("ImageInteractions.json");
        interactions = new ImageInteractionsParserObject(res);
    }

    @Test
    public void testEmpty() {
        EnumSet<DerpibooruImageInteraction.InteractionType> interactions = this.interactions.getImageInteractionsForImage(-1);
        EnumSet expected = EnumSet.noneOf(DerpibooruImageInteraction.InteractionType.class);
        assertThat(interactions, is(expected));
    }

    @Test
    public void testFavedAndUpvoted() {
        EnumSet<DerpibooruImageInteraction.InteractionType> interactions = this.interactions.getImageInteractionsForImage(1100009);
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Fave,
                                      DerpibooruImageInteraction.InteractionType.Upvote);
        assertThat(interactions, is(expected));
    }

    @Test
    public void testFaved() {
        EnumSet<DerpibooruImageInteraction.InteractionType> interactions = this.interactions.getImageInteractionsForImage(50234);
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Fave);
        assertThat(interactions, is(expected));
    }

    @Test
    public void testUpvoted() {
        EnumSet<DerpibooruImageInteraction.InteractionType> interactions = this.interactions.getImageInteractionsForImage(1100271);
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Upvote);
        assertThat(interactions, is(expected));
    }

    @Test
    public void testDownvoted() {
        EnumSet<DerpibooruImageInteraction.InteractionType> interactions = this.interactions.getImageInteractionsForImage(82342);
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Downvote);
        assertThat(interactions, is(expected));
    }
}
