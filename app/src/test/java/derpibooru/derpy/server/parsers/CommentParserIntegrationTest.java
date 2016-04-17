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
import derpibooru.derpy.ui.views.htmltextview.ImageActionSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * An integration test for {@link CommentParser}, (partly) {@link ImageFilterParserObject}, {@link ImageActionLink} and {@link ImageActionSource}.
 * <p>
 * <strong>Warning:</strong> relies on a specific implementation of {@link derpibooru.derpy.ui.views.htmltextview.ImageActionSource.SourceBuilder} that
 * uses sequential IDs (0, 1, 2 ...)
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
        testEmbeddedLinkWithoutFilter(expectedImageSource, loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
    }

    @Test
    public void testFilteredEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.singletonList(oneOfImageTagIds));
        DerpibooruComment parsedHidden = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        String expectedHiddenLink = getEmbeddedImageExpectedLink(expectedHiddenFilterImage, expectedImageSource);

        parser = new CommentParser(spoileredTagList, Collections.<Integer>emptyList());
        DerpibooruComment parsedSpoilered = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        String expectedSpoileredLink = getEmbeddedImageExpectedLink(expectedSpoileredFilterImage, expectedImageSource);

        assertExpected(parsedHidden, expectedHiddenLink);
        assertExpected(parsedSpoilered, expectedSpoileredLink);
    }

    @Test
    public void testWithoutEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<Integer>emptyList());
        DerpibooruComment parsed = parser.parseResponse(getResourceWithoutEmbeddedImage());
        assertExpected(parsed);
    }

    @Test
    public void testExternalGif() throws Exception {
        String resource = getResourceWithExternalGif();
        Element el = Jsoup.parse(resource).select("div.post-text").first().select("img").last();

        ImageActionSource.SourceBuilder sourceBuilder = new ImageActionSource.SourceBuilder();
        int id = sourceBuilder.getSourceId();
        String expectedLink = ImageActionLink.LinkInserter.getWrappedExternalGifImage(id, sourceBuilder.getImageActionSource(id, el.attr("src"))).outerHtml();
        testLinkWithoutFilter(expectedLink, resource);
    }

    @Test
    public void testEmbeddedGifNotProcessed() throws Exception {
        testEmbeddedLinkWithoutFilter(expectedImageSource + ".gif", getResourceWithEmbeddedGif());
    }

    private String getEmbeddedImageExpectedLink(String expectedFilteredSource, String expectedMainSource) {
        ImageActionSource.SourceBuilder sourceBuilder = new ImageActionSource.SourceBuilder();
        int id = sourceBuilder.getSourceId();
        return ImageActionLink.LinkInserter.getWrappedEmbeddedImage(
                id, sourceBuilder.getImageActionSource(id, expectedFilteredSource),
                sourceBuilder.getImageActionSource(id, expectedMainSource)).outerHtml();
    }

    private String getEmbeddedImageExpectedLink(String expectedMainSource) {
        ImageActionSource.SourceBuilder sourceBuilder = new ImageActionSource.SourceBuilder();
        int id = sourceBuilder.getSourceId();
        return ImageActionLink.LinkInserter.getWrappedEmbeddedImage(id, sourceBuilder.getImageActionSource(id, expectedMainSource)).outerHtml();
    }

    private void testEmbeddedLinkWithoutFilter(String imageSource, String resource) throws Exception {
        testLinkWithoutFilter(getEmbeddedImageExpectedLink(imageSource), resource);
    }

    private void testLinkWithoutFilter(String expectedLink, String resource) throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<Integer>emptyList());
        DerpibooruComment parsed = parser.parseResponse(resource);
        assertExpected(parsed, expectedLink);
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
        DerpibooruComment expectedComment = expectedCommentWithTextAppended(" \n" + expectedActionLink);
        /* samePropertyValuesAs doesn't provide diffs, which complicates visual assertion of the comment text */
        assertThat(parsedComment.getText(), is(expectedComment.getText()));
        assertThat(parsedComment, samePropertyValuesAs(expectedComment));
    }

    private void assertExpected(DerpibooruComment parsedComment) {
        /* samePropertyValuesAs doesn't provide diffs, which complicates visual assertion of the comment text */
        assertThat(parsedComment.getText(), is(expected.getText()));
        assertThat(parsedComment, samePropertyValuesAs(expected));
    }

    private DerpibooruComment expectedCommentWithTextAppended(String appendToCommentBody) {
        return new DerpibooruComment(expected.getId(), expected.getAuthor(), expected.getAuthorAvatarUrl(),
                                     expected.getText() + appendToCommentBody, expected.getPostedAt());
    }
}
