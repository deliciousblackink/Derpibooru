package derpibooru.derpy.server.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.data.types.DerpibooruTag;

public class HtmlParser {
    private String mRawHtml;

    public HtmlParser(String raw) {
        mRawHtml = raw;
    }

    public DerpibooruImageInfo readImage(int imageId) {
        Document doc = Jsoup.parse(mRawHtml);

        Element source = doc.select("input#image_source_url").first();
        String imageSourceUrl = "";
        if (source != null) {
            imageSourceUrl = source.attr("value");
        }

        Element upld = doc.select("span.image_uploader").first();
        String imageUploader;
        if (upld.select("a").first() != null) {
            imageUploader = upld.select("a").first().text();
        } else {
            imageUploader = upld.select("strong").first().text();
        }

        /* TODO: parse uploader's badges */

        Element descr = doc.select("div.image-description").first();
        String imageDescription = "";
        if (descr != null) {
            descr.select("h3").first().remove();
            imageDescription = descr.html();
        }

        Element date = doc.select("time").first();
        String imageCreatedAt = date.attr("datetime");

        Elements tags = doc.select("span[^data-tag]");
        ArrayList<DerpibooruTag> imageTags = new ArrayList<>();
        for (Element tag : tags) {
            int tagId = Integer.parseInt(tag.attr("data-tag-id"));
            int tagImgCount = getNumberOfImagesFromTagText(tag.text());
            String tagName = tag.attr("data-tag-name");
            DerpibooruTag.TagType tagType = getTagTypeFromName(tagName);

            imageTags.add(new DerpibooruTag(tagId, tagImgCount, tagName, tagType));
        }

        Elements users = doc.select("a.interaction-user-list-item");
        ArrayList<String> imageFavedBy = new ArrayList<>();
        for (Element user : users) {
            imageFavedBy.add(user.text());
        }

        return new DerpibooruImageInfo(imageId, imageSourceUrl, imageUploader,
                                       imageDescription, imageCreatedAt,
                                       imageTags, imageFavedBy);
    }

    private int getNumberOfImagesFromTagText(String renderedTagText) {
        /* alternate hairstyle (7280) +SH
         * ->
         * alternatehairstyle(7280)+SH */
        String s = renderedTagText.replaceAll("\\s", "");
        /* alternatehairstyle(7280)+SH
         * ->
         * 7280 */
        Matcher m = Pattern.compile("(?!\\()([\\d*\\.]+)(?=\\))").matcher(s);
        /* m.groupCount() - 1 to handle cases like
         * excalibur(1981)(3)+SH */
        return m.find() ? Integer.parseInt(m.group(m.groupCount() - 1)) : 0;
    }

    private DerpibooruTag.TagType getTagTypeFromName(String tagName) {
        /* TODO: add spoiler and OC tags */
        /* fixed tag names */
        switch (tagName) {
            case "anonymous artist":
                return DerpibooruTag.TagType.Artist;
            case "artist needed":
                return DerpibooruTag.TagType.Artist;
            case "edit":
                return DerpibooruTag.TagType.Artist;
            case "explicit":
                return DerpibooruTag.TagType.ContentSafety;
            case "grimdark":
                return DerpibooruTag.TagType.ContentSafety;
            case "grotesque":
                return DerpibooruTag.TagType.ContentSafety;
            case "questionable":
                return DerpibooruTag.TagType.ContentSafety;
            case "safe":
                return DerpibooruTag.TagType.ContentSafety;
            case "semi-grimdark":
                return DerpibooruTag.TagType.ContentSafety;
            case "suggestive":
                return DerpibooruTag.TagType.ContentSafety;
        }
        /* check if the tag name contains "artist:" prefix */
        if (Pattern.compile("^(artist:)")
                .matcher(tagName).find()) {
            return DerpibooruTag.TagType.Artist;
        }
        return DerpibooruTag.TagType.General;
    }
}
