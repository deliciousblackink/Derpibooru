package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.objects.ImageFilterParserObject;
import derpibooru.derpy.ui.views.htmltextview.ImageActionLink;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * An integration test for {@link CommentParser}, (partly) {@link ImageFilterParserObject} and {@link ImageActionLink}.
 */
public class CommentParserIntegrationTest {
    private static final DerpibooruComment expected =
            new DerpibooruComment(1407999, "Background Pony #B83E", "https://derpicdn.net/assets/no_avatar-1f16e058f8de3098c829dbfded69eb028fc02f52cccb886edc659e93011545fe.svg",
                                  "That’s one reason why Yume Nikki and its spiritual sequels are so popular:It’s ALL headcanon!:D",
                                  "2013-05-16T15:52:37Z");
    private static final int oneOfImageTagIds = 73173;

    private static final String expectedImageSource = "https://derpicdn.net/img/2012/12/27/194752/small.png";
    private static final String expectedSpoileredFilterImage = "https://derpicdn.net/media/W1siZiIsIjIwMTQvMDEvMDcvMjFfMTBfNTZfMjkxX3NlbWlfZ3JpbWRhcmsucG5nIl0sWyJwIiwidGh1bWIiLCIyNTB4MjUwIl1d.png";
    private static final String expectedHiddenFilterImage = ImageFilterParserObject.HIDDEN_TAG_IMAGE_RESOURCE_URI;

    private static final ArrayList<DerpibooruTagDetailed> spoileredTagList =
            new ArrayList<DerpibooruTagDetailed>() {{ add(new DerpibooruTagDetailed(oneOfImageTagIds, 0, "", "", "", expectedSpoileredFilterImage)); }};

    TestResourceLoader loader;

    @Before
    public void setUp() {
        loader = new TestResourceLoader();
    }

    @Test
    public void testUnfilteredEmbeddedImage() throws Exception {
        testEmbeddedWithoutFilter(expectedImageSource, loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
    }

    @Test
    public void testFilteredEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.singletonList(oneOfImageTagIds));
        DerpibooruComment parsedHidden = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        String expectedHiddenLink = ImageActionLink.LinkInserter.getWrappedEmbeddedImage(expectedHiddenFilterImage, expectedImageSource).outerHtml();

        parser = new CommentParser(spoileredTagList, Collections.<Integer>emptyList());
        DerpibooruComment parsedSpoilered = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        String expectedSpoileredLink = ImageActionLink.LinkInserter.getWrappedEmbeddedImage(expectedSpoileredFilterImage, expectedImageSource).outerHtml();

        assertExpected(parsedHidden, expectedHiddenLink);
        assertExpected(parsedSpoilered, expectedSpoileredLink);
    }

    @Test
    public void testWithoutEmbeddedImage() throws Exception {
        testWithoutFilter(expected, getResourceWithoutEmbeddedImage());
    }

    @Test
    public void testExternalGif() throws Exception {
        String resource = getResourceWithExternalGif();
        Element el = Jsoup.parse(resource).select("div.post-text").first().select("img").last();
        ImageActionLink.LinkInserter.wrapGifImage(el);
        testLinkWithoutFilter(el.parent().outerHtml(), resource);
    }

    @Test
    public void testEmbeddedGifNotProcessed() throws Exception {
        testEmbeddedWithoutFilter(expectedImageSource + ".gif", getResourceWithEmbeddedGif());
    }

    private void testWithoutFilter(DerpibooruComment expectedComment, String resource) throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<Integer>emptyList());
        DerpibooruComment parsed = parser.parseResponse(resource);
        assertThat(parsed, samePropertyValuesAs(expectedComment));
    }

    private void testLinkWithoutFilter(String expectedLink, String resource) throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<Integer>emptyList());
        DerpibooruComment parsed = parser.parseResponse(resource);
        assertExpected(parsed, expectedLink);
    }

    private void testEmbeddedWithoutFilter(String imageSource, String resource) throws Exception {
        testLinkWithoutFilter(ImageActionLink.LinkInserter.getWrappedEmbeddedImage(imageSource).outerHtml(), resource);
    }

    private String getResourceWithEmbeddedGif() {
        String source = loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html");
        Document doc = Jsoup.parse(source);
        Element img = doc.select("div.image-show").first().select("img").first();
        img.attr("src", (img.attr("src") + ".gif"));
        return doc.outerHtml();
    }

    private String getResourceWithExternalGif() {
        String source = loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html");
        Document doc = Jsoup.parse(source);
        Element postBody = doc.select("div.post-text").first();
        postBody.select("div.image-show-container").first().remove();
        postBody.appendChild(new Element(Tag.valueOf("img"), "").attr("src", expectedImageSource + ".gif"));
        return doc.outerHtml();
    }

    private String getResourceWithoutEmbeddedImage() {
        String source = loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html");
        Document doc = Jsoup.parse(source);
        doc.select("div.image-show-container").first().remove();
        return doc.outerHtml();
    }

    private void assertExpected(DerpibooruComment parsedComment, String expectedActionLink) {
        /* it is an expected behavior that a line break is added in front of the image */
        assertThat(parsedComment, samePropertyValuesAs(expectedCommentWithTextAppended(" \n" + expectedActionLink)));
    }

    private DerpibooruComment expectedCommentWithTextAppended(String appendToCommentBody) {
        return new DerpibooruComment(expected.getId(), expected.getAuthor(), expected.getAuthorAvatarUrl(),
                                     expected.getText() + appendToCommentBody, expected.getPostedAt());
    }
}
