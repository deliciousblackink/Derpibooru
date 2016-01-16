package derpibooru.derpy.server.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.types.DerpibooruTag;
import derpibooru.derpy.data.types.ImageFullInfo;

public class HtmlParser {
    private String mRawHtml;

    public HtmlParser(String raw) {
        mRawHtml = raw;
    }

    public ImageFullInfo readImage() {
        Document doc = Jsoup.parse(mRawHtml);

        Element imgUrl = doc.select("div.image-show").first();
        String url = imgUrl.attr("data-download-uri");

        /* TODO: parse uploader's badges */

        Elements imgTags = doc.select("span[^data-tag]");
        ArrayList<DerpibooruTag> tags = new ArrayList<>();
        for (Element tag : imgTags) {
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

            tags.add(new DerpibooruTag(id, imgCount, name));
        }

        Elements users = doc.select("a.interaction-user-list-item");
        ArrayList<String> favedBy = new ArrayList<>();
        for (Element user : users) {
            favedBy.add(user.text());
        }

        return new ImageFullInfo(url, tags, favedBy);
    }
}
