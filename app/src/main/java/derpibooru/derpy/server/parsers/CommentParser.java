package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruComment;

public class CommentParser implements ServerResponseParser<DerpibooruComment> {
    private static final Pattern PATTERN_COMMENT_ID = Pattern.compile("^(?:comment_)([\\d]*)");

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

    private String parseCommentBody(Element commentContent) {
        return commentContent.select("div.post-text").first().html();
    }

    private String parsePostedAt(Element commentOptions) {
        Element time = commentOptions.select("div").first().select("time").first();
        return time.attr("datetime");
    }
}
