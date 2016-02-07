package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.data.server.DerpibooruTag;

public class ImageInfoParser implements ServerResponseParser {
    public Object parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);

        int imageId = parseImageId(doc);
        String imageSourceUrl = parseSourceUrl(doc);
        String imageUploader = parseUploader(doc);
        String imageDescription = parseDescription(doc);
        String imageCreatedAt = parseDateCreatedAt(doc);
        ArrayList<String> imageFavedBy = parseFavedBy(doc);
        ArrayList<DerpibooruTag> imageTags = parseTags(doc);

        return new DerpibooruImageInfo(imageId, imageSourceUrl, imageUploader,
                                       imageDescription, imageCreatedAt,
                                       imageTags, imageFavedBy);
    }

    private int parseImageId(Document doc) {
        String title = doc.select("title").first().text();
        Matcher m = Pattern.compile("^(?:#)([\\d]*)").matcher(title);
        /* m.group(0) is '#000000', m.group(1) is '000000' */
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private String parseSourceUrl(Document doc) {
        Element source = doc.select("input#image_source_url").first();
        String imageSourceUrl = "";
        if (source != null) {
            imageSourceUrl = source.attr("value");
        }
        return imageSourceUrl;
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
        return imageTags;
    }

    private int parseNumberOfImagesFromTagText(String renderedTagText) {
        /* alternate hairstyle (7280) +SH
         * ->
         * alternatehairstyle(7280)+SH */
        String s = renderedTagText.replaceAll("\\s", "");
        /* alternatehairstyle(7280)+SH
         * ->
         * 7280 */
        Matcher m = Pattern.compile("(?!\\()([\\d*\\.]+)(?=\\))").matcher(s);
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
}
