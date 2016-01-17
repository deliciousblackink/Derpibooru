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

        Element upld = doc.select("span.image_uploader").first().select("a").first();
        String imageUploader = upld.text();
        /* TODO: parse uploader's badges */

        Element descr = doc.select("div.image-description").first();
        String imageDescription = "";
        if (descr != null) {
            descr.select("h3").first().remove();
            imageDescription = descr.html();
        }

        Elements tags = doc.select("span[^data-tag]");
        ArrayList<DerpibooruTag> imageTags = new ArrayList<>();
        for (Element tag : tags) {
            int id = Integer.parseInt(tag.attr("data-tag-id"));
            String name = tag.attr("data-tag-name");

            /* alternate hairstyle (7280) +SH
             * ->
             * alternatehairstyle(7280)+SH */
            String s = tag.text().replaceAll("\\s", "");
            /* alternatehairstyle(7280)+SH
             * ->
             * 7280 */
            Matcher m = Pattern.compile("(?!\\()([\\d*\\.]+)(?=\\))").matcher(s);
            /* m.groupCount() - 1 to handle cases like
             * excalibur(1981)(3)+SH */
            int imgCount = m.find() ? Integer.parseInt(m.group(m.groupCount() - 1)) : 0;

            imageTags.add(new DerpibooruTag(id, imgCount, name));
        }

        Elements users = doc.select("a.interaction-user-list-item");
        ArrayList<String> imageFavedBy = new ArrayList<>();
        for (Element user : users) {
            imageFavedBy.add(user.text());
        }

        return new DerpibooruImageInfo(imageId, imageSourceUrl, imageUploader,
                                       imageDescription, imageTags, imageFavedBy);
    }
}
