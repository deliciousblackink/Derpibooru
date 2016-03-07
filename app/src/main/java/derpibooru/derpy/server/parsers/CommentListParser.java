package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruComment;

public class CommentListParser implements ServerResponseParser<List<DerpibooruComment>> {
    @Override
    public List<DerpibooruComment> parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        if (doc.select("div.metabar").first() == null) {
            return new ArrayList<>();
        }
        List<DerpibooruComment> commentList = new ArrayList<>();
        /* TODO: parse comment author's badges */
        Elements comments = doc.select("div.post-content");
        Elements commentOptions = doc.select("div.post-options");
        int commentCount = comments.size();
        for (int x = 0; x < commentCount; x++) {
            String author = parseAuthor(comments.get(x));
            String avatarUrl = parseAvatarUrl(comments.get(x));
            String text = parseCommentBody(comments.get(x));
            String postedAt = parsePostedAt(commentOptions.get(x));
            commentList.add(new DerpibooruComment(author, avatarUrl, text, postedAt));
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
