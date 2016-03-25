package derpibooru.derpy.server.parsers;

import org.junit.Before;
import org.junit.Test;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruComment;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class CommentParserTest {
    private static final DerpibooruComment expected =
            new DerpibooruComment(1407999, "Background Pony #B83E", "https://derpicdn.net/assets/no_avatar-1f16e058f8de3098c829dbfded69eb028fc02f52cccb886edc659e93011545fe.svg",
                                  "That’s one reason why Yume Nikki and its spiritual sequels are so popular:It’s ALL headcanon!:D",
                                  "2013-05-16T15:52:37Z");

    private DerpibooruComment parsed;

    @Before
    public void setUp() throws Exception {
        TestResourceLoader loader = new TestResourceLoader();
        String comments = loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html");
        CommentParser parser = new CommentParser();
        parsed = parser.parseResponse(comments);
    }

    @Test
    public void test() {
        assertThat(parsed, samePropertyValuesAs(expected));
    }
}
