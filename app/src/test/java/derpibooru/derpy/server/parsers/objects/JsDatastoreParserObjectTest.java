package derpibooru.derpy.server.parsers.objects;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JsDatastoreParserObjectTest {
    private JsDatastoreParserObject loggedIn;
    private JsDatastoreParserObject loggedOut;

    @Before
    public void setUp() {
        TestResourceLoader loader = new TestResourceLoader();
        Document loggedInDoc = Jsoup.parse(loader.readTestResourceFile("JsDatastoreLoggedIn.html"));
        loggedIn = new JsDatastoreParserObject(loggedInDoc);
        Document loggedOutDoc = Jsoup.parse(loader.readTestResourceFile("JsDatastoreLoggedOut.html"));
        loggedOut = new JsDatastoreParserObject(loggedOutDoc);
    }

    @Test
    public void testUsername() {
        assertThat(loggedIn.getUsername(), is("TestUserName = Test"));
        assertThat(loggedOut.getUsername(), is(""));
    }

    @Test
    public void testFilterId() {
        assertThat(loggedIn.getFilterId(), is(100073));
        assertThat(loggedOut.getFilterId(), is(100073));
    }

    @Test
    public void testHiddenTagIds() throws JSONException {
        assertThat(loggedIn.getHiddenTagIds(), is(Arrays.asList(
                61060, 89379, 20417, 86723, 94478)));
        assertThat(loggedOut.getHiddenTagIds(), is(Collections.<Integer>emptyList()));
    }

    @Test
    public void testSpoileredTagIds() throws JSONException {
        assertThat(loggedIn.getSpoileredTagIds(), is(Arrays.asList(
                41133, 41161, 42773, 114937, 173118, 173119, 173120, 173121, 173122, 173123, 173124)));
        assertThat(loggedOut.getSpoileredTagIds(), is(Collections.<Integer>emptyList()));
    }

    @Test
    public void testInteractions() throws JSONException {
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Fave,
                                      DerpibooruImageInteraction.InteractionType.Upvote);
        ImageInteractionsParserObject interactionsHelper = new ImageInteractionsParserObject(
                loggedIn.getInteractions().toString());
        assertThat(interactionsHelper.getImageInteractionsForImage(10005), is(expected));
        assertNotNull(loggedOut.getInteractions());
    }
}
