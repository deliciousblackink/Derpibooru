package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruComment;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class CommentListParserTest {
    private static final DerpibooruComment expected =
            new DerpibooruComment("Silent Wing", "https://derpicdn.net/assets/no_avatar-1f16e058f8de3098c829dbfded69eb028fc02f52cccb886edc659e93011545fe.svg",
                                  "<a href=\"#comment_2588558\">@PwnyPony</a> \n<br> The look on his face says, \"I don’t think that went as planned. Let me step back.\" XD",
                                  "2014-06-17T22:49:26Z");

    private DerpibooruComment parsed;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String comments = loader.readTestResourceFile("SampleImageCommentsResponse.html");
        CommentListParser parser = new CommentListParser();
        parsed = parser.parseResponse(comments).get(0);
    }

    @Test
    public void test() {
        assertThat(parsed, samePropertyValuesAs(expected));
    }
}
