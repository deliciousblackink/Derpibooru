package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
        Elements commentsRaw = doc.select("article");

        int commentCount = commentsRaw.size();
        List<DerpibooruComment> commentList = new ArrayList<>(commentCount);

        CommentParser parser = new CommentParser();
        for (int i = 0; i < commentCount; i++) {
            commentList.add(parser.parseResponse(commentsRaw.get(i).outerHtml()));
        }
        return commentList;
    }
}
