package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.TestResourceLoader;
import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.objects.ImageFilterParserObject;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedFilteredImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ExternalGifImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.HtmlImageActionCreator;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * An integration test for {@link CommentParser}, (partly) {@link ImageFilterParserObject}, {@link ImageAction}s and {@link HtmlImageActionCreator}.
 */
public class CommentParserIntegrationTest {
    private static final DerpibooruComment expected =
            new DerpibooruComment(1407999, "Background Pony #B83E", UserDataParser.DEFAULT_AVATAR_SRC,
                                  "That’s one reason why Yume Nikki and its spiritual sequels are so popular:It’s ALL headcanon!:D",
                                  "2013-05-16T15:52:37Z");
    private static final int oneOfImageTagIds = 73173;
    private static final int embeddedImageId = 194752;

    private static final String expectedImageSource = "https://derpicdn.net/img/2012/12/27/194752/small.png";
    private static final String expectedSpoileredFilterImage = "https://derpicdn.net/media/W1siZiIsIjIwMTQvMDEvMDcvMjFfMTBfNTZfMjkxX3NlbWlfZ3JpbWRhcmsucG5nIl0sWyJwIiwidGh1bWIiLCIyNTB4MjUwIl1d.png";
    private static final String expectedHiddenFilterImage = ImageFilterParserObject.HIDDEN_TAG_IMAGE_RESOURCE_URI;

    private static final String expectedSpoileredTagName = "firefly";
    private static final List<DerpibooruTagDetailed> spoileredTagList = new ArrayList<DerpibooruTagDetailed>() {{
        add(new DerpibooruTagDetailed(oneOfImageTagIds, 0, expectedSpoileredTagName, "", "", expectedSpoileredFilterImage)); }};
    private static final String expectedHiddenTagName = "feast";
    private static final List<DerpibooruTagDetailed> hiddenTagList =  new ArrayList<DerpibooruTagDetailed>() {{
        add(new DerpibooruTagDetailed(oneOfImageTagIds, 0, expectedHiddenTagName, "", "", /* the spoiler image does not matter for hidden tags */ "")); }};

    TestResourceLoader loader;

    @Before
    public void setUp() {
        loader = new TestResourceLoader();
    }

    @Test
    public void testUnfilteredEmbeddedImage() throws Exception {
        testEmbeddedLinkWithoutFilter(embeddedImageId, expectedImageSource, loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
    }

    @Test
    public void testHiddenEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), hiddenTagList);
        DerpibooruComment parsedHidden = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        assertExpected(parsedHidden, new EmbeddedFilteredImageAction(embeddedImageId, expectedImageSource, expectedHiddenFilterImage, expectedHiddenTagName));
    }

    @Test
    public void testSpoileredEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(spoileredTagList, Collections.<DerpibooruTagDetailed>emptyList());
        DerpibooruComment parsedSpoilered = parser.parseResponse(loader.readTestResourceFile("SampleImageCommentAJAXCallResponse.html"));
        assertExpected(parsedSpoilered, new EmbeddedFilteredImageAction(embeddedImageId, expectedImageSource, expectedSpoileredFilterImage, expectedSpoileredTagName));
    }

    @Test
    public void testWithoutEmbeddedImage() throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<DerpibooruTagDetailed>emptyList());
        DerpibooruComment parsed = parser.parseResponse(getResourceWithoutEmbeddedImage());
        assertExpected(parsed);
    }

    @Test
    public void testExternalGif() throws Exception {
        String resource = getResourceWithExternalGif();
        String gifSource = Jsoup.parse(resource).select(".communication__body__text").first().select("img").last().attr("src");
        testLinkWithoutFilter(new ExternalGifImageAction(gifSource), resource);
    }

    @Test
    public void testEmbeddedGifNotProcessed() throws Exception {
        testEmbeddedLinkWithoutFilter(embeddedImageId, expectedImageSource + ".gif", getResourceWithEmbeddedGif());
    }

    private void testEmbeddedLinkWithoutFilter(int imageId, String imageSource, String resource) throws Exception {
        testLinkWithoutFilter(new EmbeddedImageAction(imageId, imageSource), resource);
    }

    private void testLinkWithoutFilter(ImageAction expectedLink, String resource) throws Exception {
        CommentParser parser = new CommentParser(Collections.<DerpibooruTagDetailed>emptyList(), Collections.<DerpibooruTagDetailed>emptyList());
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
        Element postBody = doc.select(".communication__body__text").first();
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

    private void assertExpected(DerpibooruComment parsedComment, ImageAction expectedAction) {
        /* it is an expected behavior that a line break is added in front of the image */
        DerpibooruComment expectedComment = expectedCommentWithTextAppended(
                " \n" + HtmlImageActionCreator.getImageActionElement(expectedAction.toStringRepresentation()));
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