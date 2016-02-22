package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruImageComment;

public class ImageCommentsParser implements ServerResponseParser<List<DerpibooruImageComment>> {
    @Override
    public List<DerpibooruImageComment> parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        if (doc.select("div.metabar").first() == null) {
            return new ArrayList<>();
        }
        List<DerpibooruImageComment> commentList = new ArrayList<>();
        /* TODO: parse comment author's badges */
        Elements comments = doc.select("div.post-content");
        Elements commentOptions = doc.select("div.post-options");
        int commentCount = comments.size();
        for (int x = 0; x < commentCount; x++) {
            String author = parseAuthor(comments.get(x));
            String avatarUrl = parseAvatarUrl(comments.get(x));
            String text = parseText(comments.get(x));
            String postedAt = parsePostedAt(commentOptions.get(x));
            commentList.add(new DerpibooruImageComment(author, avatarUrl, text, postedAt));
        }
        return commentList;
    }

    private String parseAuthor(Element commentContent) {
        Element authorLink = commentContent.select("span.post-author").first().select("a").first();
        return (authorLink != null) ? authorLink.text()
                                    /* author is background pony */
                                    : commentContent.select("span.post-author").first().text();
    }

    private String parseAvatarUrl(Element commentContent) {
        return commentContent.select("img").first().attr("src");
    }

    private String parseText(Element commentContent) {
        return commentContent.select("div.post-text").first().text();
    }

    private String parsePostedAt(Element commentOptions) {
        String info = commentOptions.select("div").first().text();
        Matcher m = Pattern.compile("^(?:Posted\\s)(.*?)(?:\\s\\u0095)").matcher(info);
        return m.find() ? m.group(1) : "";
    }
}
