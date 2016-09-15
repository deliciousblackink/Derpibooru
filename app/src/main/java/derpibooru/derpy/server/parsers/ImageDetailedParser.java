package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTag;
import derpibooru.derpy.server.parsers.objects.ImageInteractionsParserObject;
import derpibooru.derpy.server.parsers.objects.UserScriptParserObject;
import derpibooru.derpy.server.parsers.objects.HeaderParserObject;

public class ImageDetailedParser implements ServerResponseParser<DerpibooruImageDetailed> {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s");
    private static final Pattern PATTERN_TAG_NUMBER_OF_IMAGES = Pattern.compile("(?!\\()([\\d*\\.]+)(?=\\))");

    private ImageInteractionsParserObject mInteractions;

    @Override
    public DerpibooruImageDetailed parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);

        HeaderParserObject header = new HeaderParserObject(doc.select(".header").first().html());
        if (header.isLoggedIn()) {
            UserScriptParserObject script =
                    new UserScriptParserObject(doc.select("body").select("script").last().html());
            mInteractions = new ImageInteractionsParserObject(script.getInteractions().toString());
        }

        String imageSourceUrl = parseSourceUrl(doc);
        String imageDownloadUrl = parseDownloadUrl(doc);
        String imageUploader = parseUploader(doc);
        String imageDescription = parseDescription(doc);
        String imageCreatedAt = parseDateCreatedAt(doc);
        ArrayList<String> imageFavedBy = parseFavedBy(doc);
        ArrayList<DerpibooruTag> imageTags = parseTags(doc);

        return new DerpibooruImageDetailed(getImage(doc), imageSourceUrl, imageDownloadUrl, imageUploader,
                                           imageDescription, imageCreatedAt, imageTags, imageFavedBy);
    }

    private String parseSourceUrl(Document doc) {
        Element source = doc.select("input#image_source_url").first();
        String imageSourceUrl = "";
        if (source != null) {
            imageSourceUrl = source.attr("value");
        }
        return imageSourceUrl;
    }

    private String parseDownloadUrl(Document doc) throws JSONException {
        Element imageContainer = getImageContainer(doc);
        return ("https:" + new JSONObject(imageContainer.attr("data-uris")).getString("full"));
    }

    private String parseUploader(Document doc) {
        Element upld = doc.select("span.image_uploader").first();
        if (upld.select("a").first() != null) {
            return upld.select("a").first().text();
        } else {
            return upld.select("strong").first().text();
        }
        /* TODO: parse uploader's badges */
    }

    private String parseDescription(Document doc) {
        Element descr = doc.select("div.image-description").first();
        String imageDescription = "";
        if (descr != null) {
            descr.select("h3").first().remove();
            imageDescription = descr.html();
        }
        return imageDescription;
    }

    private String parseDateCreatedAt(Document doc) {
        Element date = doc.select("time").first();
        return date.attr("datetime");
    }

    private ArrayList<String> parseFavedBy(Document doc) {
        Elements users = doc.select("a.interaction-user-list-item");
        ArrayList<String> imageFavedBy = new ArrayList<>();
        for (Element user : users) {
            imageFavedBy.add(user.text());
        }
        return imageFavedBy;
    }

    private ArrayList<DerpibooruTag> parseTags(Document doc) {
        Elements tags = doc.select("span[^data-tag]");
        ArrayList<DerpibooruTag> imageTags = new ArrayList<>();
        for (Element tag : tags) {
            int tagId = Integer.parseInt(tag.attr("data-tag-id"));
            int tagImgCount = parseNumberOfImagesFromTagText(tag.text());
            String tagName = tag.attr("data-tag-name");
            imageTags.add(new DerpibooruTag(tagId, tagImgCount, tagName));
        }
        Collections.sort(imageTags, new DerpibooruTagTypeComparator(false));
        return imageTags;
    }

    private int parseNumberOfImagesFromTagText(String renderedTagText) {
        /* alternate hairstyle (7280) +SH
         * ->
         * alternatehairstyle(7280)+SH */
        String s = PATTERN_WHITESPACE.matcher(renderedTagText).replaceAll("");
        /* alternatehairstyle(7280)+SH
         * ->
         * 7280 */
        Matcher m = PATTERN_TAG_NUMBER_OF_IMAGES.matcher(s);
        int numberOfImages = 0;
        while (m.find()) {
             /* handle cases like
              * excalibur(1981)(3)+SH
              * where the matcher will have two "find()" matches
              */
            numberOfImages = Integer.parseInt(m.group(m.groupCount() - 1));
        }
        return numberOfImages;
    }

    private DerpibooruImageThumb getImage(Document doc) throws JSONException {
        Element imageContainer = getImageContainer(doc);

        int imageId = Integer.parseInt(imageContainer.attr("data-image-id"));
        EnumSet interactions = EnumSet.noneOf(DerpibooruImageInteraction.InteractionType.class);
        if (mInteractions != null) {
            interactions = mInteractions.getImageInteractionsForImage(imageId);
        }

        DerpibooruImageThumb thumb = new DerpibooruImageThumb(
                imageId,
                Integer.parseInt(imageContainer.attr("data-upvotes")),
                Integer.parseInt(imageContainer.attr("data-downvotes")),
                Integer.parseInt(imageContainer.attr("data-faves")),
                Integer.parseInt(imageContainer.attr("data-comment-count")),
                "https:" + new JSONObject(imageContainer.attr("data-uris")).getString("thumb"),
                "https:" + new JSONObject(imageContainer.attr("data-uris")).getString("large"),
                "", interactions
        );
        return thumb;
    }

    private Element getImageContainer(Document doc) {
        return doc.select("div.image-show-container").first();
    }
}
