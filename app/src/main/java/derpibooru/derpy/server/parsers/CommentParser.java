package derpibooru.derpy.server.parsers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.objects.ImageFilterParserObject;

public class CommentParser implements ServerResponseParser<DerpibooruComment> {
    private static final String HIDDEN_TAG_LINK = "\\hidden\\%s\\main\\%s";
    private static final String SPOILERED_TAG_LINK = "spoilered\\%s\\main\\%s";
    private static final String IMAGE_LINK = "main\\%s";

    private static final Pattern PATTERN_COMMENT_ID = Pattern.compile("^(?:comment_)([\\d]*)");
    private static final String IMAGE_CONTAINER_SELECTOR = "div.image-show-container";

    private final ImageFilterParserObject mFilter;

    public CommentParser(List<DerpibooruTagDetailed> spoileredTags, List<Integer> hiddenTagIds) {
        mFilter = new ImageFilterParserObject(spoileredTags, hiddenTagIds);
    }

    @Override
    public DerpibooruComment parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);

        Element comment = doc.select("div.post-content").first();
        Element commentOptions = doc.select("div.post-options").first();

        int id = parseId(doc.select("article").first());
        String author = parseAuthor(comment);
        String avatarUrl = parseAvatarUrl(comment);
        String text = parseCommentBody(comment);
        String postedAt = parsePostedAt(commentOptions);
        return new DerpibooruComment(id, author, avatarUrl, text, postedAt);
    }

    private int parseId(Element article) {
        Matcher m = PATTERN_COMMENT_ID.matcher(article.attr("id"));
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private String parseAuthor(Element commentContent) {
        /* TODO: parse comment author's badges */
        return commentContent.select(".post-author").first().text().trim();
    }

    private String parseAvatarUrl(Element commentContent) {
        return "https:" + commentContent.select("img").first().attr("src");
    }

    private String parseCommentBody(Element commentContent) throws JSONException {
        Element post = commentContent.select("div.post-text").first();
        post.select("span.spoiler").tagName("spoiler").removeAttr("class");
        processEmbeddedImages(post);
        return post.html();
    }

    private String parsePostedAt(Element commentOptions) {
        Element time = commentOptions.select("div").first().select("time").first();
        return time.attr("datetime");
    }

    private Element processEmbeddedImages(Element postBody) throws JSONException {
        while (!postBody.select(IMAGE_CONTAINER_SELECTOR).isEmpty()) {
            Element imageContainer = postBody.select(IMAGE_CONTAINER_SELECTOR).get(0);
            JSONArray imageTags = new JSONArray(imageContainer.attr("data-image-tags"));

            String hiddenTagImage = mFilter.getImageHiddenUrl(imageTags);
            String spoileredTagImage = mFilter.getImageSpoilerUrl(imageTags);
            String mainImage = getImageInContainer(imageContainer, "div.image-show");

            postBody.select(IMAGE_CONTAINER_SELECTOR).get(0).replaceWith(
                    getEmbeddedImageElement(hiddenTagImage, spoileredTagImage, mainImage));
        }
        return postBody;
    }

    private Element getEmbeddedImageElement(@NonNull String hidden, @NonNull String spoilered, @NonNull String mainImage) {
        String link;
        String source;
        if (!hidden.isEmpty()) {
            link = String.format(HIDDEN_TAG_LINK, hidden, mainImage);
            source = "https:" + hidden;
        } else if (!spoilered.isEmpty()) {
            link = String.format(SPOILERED_TAG_LINK, spoilered, mainImage);
            source = "https:" + spoilered;
        } else {
            link = String.format(IMAGE_LINK, mainImage);
            source = "https:" + mainImage;
        }
        return new Element(Tag.valueOf("a"), "")
                .attr("href", link)
                .appendChild(
                        new Element(Tag.valueOf("img"), "")
                                .attr("src", source));
    }

    @NonNull
    private String getImageInContainer(Element imageContainer, String elementSelector) {
        Element hiddenContainer = imageContainer.select(elementSelector).first();
        return hiddenContainer.select("img").attr("src");
    }
}
