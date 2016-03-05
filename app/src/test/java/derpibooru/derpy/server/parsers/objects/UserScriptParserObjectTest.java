package derpibooru.derpy.server.parsers.objects;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class UserScriptParserObjectTest {
    private UserScriptParserObject loggedInScript;
    private UserScriptParserObject loggedOutScript;

    @Before
    public void setUp() {
        TestResourceLoader loader = new TestResourceLoader();
        String loggedIn = loader.readTestResourceFile("UserScriptLoggedIn.js");
        loggedInScript = new UserScriptParserObject(loggedIn);
        String loggedOut = loader.readTestResourceFile("UserScriptLoggedOut.js");
        loggedOutScript = new UserScriptParserObject(loggedOut);
    }

    @Test
    public void testUsername() {
        assertThat(loggedInScript.getUsername(), is("TestUserName = Test"));
        assertThat(loggedOutScript.getUsername(), is(""));
    }

    @Test
    public void testAvatarUrl() {
        assertThat(loggedInScript.getAvatarUrl(), is("https://derpicdn.net/avatars/examplePath.png"));
        assertThat(loggedOutScript.getAvatarUrl(), is("https://derpicdn.net/assets/no_avatar.svg"));
    }

    @Test
    public void testFilterId() {
        assertThat(loggedInScript.getFilterId(), is(100073));
        assertThat(loggedOutScript.getFilterId(), is(100073));
    }

    @Test
    public void testSpoileredTagIds() throws JSONException {
        assertThat(loggedInScript.getSpoileredTagIds(), is(Arrays.asList(
                41133, 41161, 42773, 114937, 173118, 173119, 173120, 173121, 173122, 173123, 173124)));
        assertThat(loggedOutScript.getSpoileredTagIds(), is(Collections.<Integer>emptyList()));
    }

    @Test
    public void testInteractions() throws JSONException {
        EnumSet expected = EnumSet.of(DerpibooruImageInteraction.InteractionType.Fave,
                                      DerpibooruImageInteraction.InteractionType.Upvote);
        ImageInteractionsParserObject interactionsHelper = new ImageInteractionsParserObject(
                loggedInScript.getInteractions().toString());
        assertThat(interactionsHelper.getImageInteractionsForImage(10005), is(expected));
        assertNull(loggedOutScript.getInteractions());
    }
}
