package derpibooru.derpy.server.parsers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.objects.ImageFilterParserObject;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedFilteredImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ExternalGifImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.HtmlImageActionCreator;

public class CommentParser implements ServerResponseParser<DerpibooruComment> {
    private static final Pattern PATTERN_COMMENT_ID = Pattern.compile("^(?:comment_)([\\d]*)");
    private static final String IMAGE_CONTAINER_SELECTOR = "div.image-show-container";

    private final ImageFilterParserObject mFilter;

    public CommentParser(List<DerpibooruTagDetailed> spoileredTags, List<DerpibooruTagDetailed> hiddenTagIds) {
        mFilter = new ImageFilterParserObject(spoileredTags, hiddenTagIds);
    }

    @Override
    public DerpibooruComment parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);

        Element comment = doc.select(".communication").first();
        Element commentOptions = doc.select(".communication__options").first();

        int id = parseId(doc.select(".communication").first());
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
        return commentContent.select(".communication__body__sender-name").first().text().trim();
    }

    private String parseAvatarUrl(Element commentContent) {
        return "https:" + commentContent.select(".image-constrained img").first().attr("src");
    }

    private String parseCommentBody(Element commentContent) throws JSONException {
        Element post = commentContent.select(".communication__body__text").first();
        post.select("span.spoiler").tagName("spoiler").removeAttr("class");
        processCommentImages(post);
        return post.html();
    }

    private String parsePostedAt(Element commentOptions) {
        Element time = commentOptions.select("time").first();
        return time.attr("datetime");
    }

    private void processCommentImages(Element postBody) throws JSONException {
        processExternalImages(postBody);
        processEmbeddedImages(postBody);
    }

    private void processEmbeddedImages(Element postBody) throws JSONException {
        while (!postBody.select(IMAGE_CONTAINER_SELECTOR).isEmpty()) {
            Element imageContainer = postBody.select(IMAGE_CONTAINER_SELECTOR).get(0);
            Element imageShow = imageContainer.select("div.image-show").first();
            JSONArray imageTags = new JSONArray(imageContainer.attr("data-image-tags"));

            int imageId = Integer.parseInt(
                    imageShow.select("a").first().attr("href").substring(1));
            String mainImage = "https:" + imageShow.select("img").attr("src");
            String filterImage = mFilter.getHiddenTagImageUrl(imageTags);
            String filteredTagName = null;
            if (filterImage.isEmpty()) {
                filterImage = mFilter.getSpoileredTagImageUrl(imageTags);
                if (filterImage.isEmpty()) {
                    filterImage = null;
                } else {
                    filteredTagName = mFilter.getSpoileredTagName(imageTags);
                }
            } else {
                filteredTagName = mFilter.getHiddenTagName(imageTags);
            }

            postBody.select(IMAGE_CONTAINER_SELECTOR).get(0).replaceWith(
                    getEmbeddedImageElement(imageId, mainImage, filterImage, filteredTagName));
        }
    }

    private void processExternalImages(Element postBody) {
        /* select all GIF images that are not embedded (do not have "data-image-id" attribute) */
        for (Element image : postBody.select("img:not([data-image-id])[src~=([^/]*(gif.*))$]")) {
            String actionLink = new ExternalGifImageAction(image.attr("src")).toStringRepresentation();
            image.replaceWith(HtmlImageActionCreator.getImageActionElement(actionLink));
        }
    }

    private Element getEmbeddedImageElement(int imageId, @NonNull String mainImage,
                                            @Nullable String filterImage, @Nullable String filteredTagName) {
        String imageLink;
        if (filterImage != null) {
            imageLink = new EmbeddedFilteredImageAction(imageId, mainImage, filterImage, filteredTagName).toStringRepresentation();
        } else {
            imageLink = new EmbeddedImageAction(imageId, mainImage).toStringRepresentation();
        }
        return HtmlImageActionCreator.getImageActionElement(imageLink);
    }
}
