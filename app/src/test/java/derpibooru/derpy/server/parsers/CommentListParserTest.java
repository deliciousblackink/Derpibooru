package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruComment;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class CommentListParserTest {
    private static final DerpibooruComment expectedPostedByRegisteredUser =
            new DerpibooruComment(2588576, "Silent Wing", "https://derpicdn.net/assets/no_avatar-1f16e058f8de3098c829dbfded69eb028fc02f52cccb886edc659e93011545fe.svg",
                                  "<a href=\"#comment_2588558\">@PwnyPony</a> \n<br> The look on his face says, \"I don’t think that went as planned. Let me step back.\" XD",
                                  "2014-06-17T22:49:26Z");
    private static final DerpibooruComment expectedAnon =
            new DerpibooruComment(2622311, "Background Pony #DD72", "https://derpicdn.net/assets/no_avatar.svg", "The guy next to Trixie tried to throw a tactical rock at it. \n<spoiler>\n han solo does something in tfa\n</spoiler>",
                                  "2014-06-26T21:01:36Z");

    private DerpibooruComment[] parsed = new DerpibooruComment[2];

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String comments = loader.readTestResourceFile("SampleImageCommentsResponse.html");
        CommentListParser parser = new CommentListParser(Collections.<Integer>emptyList(), Collections.<Integer>emptyList());
        parsed[0] = parser.parseResponse(comments).get(0);
        parsed[1] = parser.parseResponse(comments).get(1);
    }

    @Test
    public void test() {
        assertThat(parsed[0], samePropertyValuesAs(expectedPostedByRegisteredUser));
        assertThat(parsed[1], samePropertyValuesAs(expectedAnon));
    }
}
