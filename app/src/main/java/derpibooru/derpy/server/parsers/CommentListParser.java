package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class CommentListParser implements ServerResponseParser<List<DerpibooruComment>> {
    private final CommentParser mCommentParser;

    public CommentListParser(List<DerpibooruTagDetailed> spoileredTags, List<Integer> hiddenTagIds) {
        mCommentParser = new CommentParser(spoileredTags, hiddenTagIds);
    }

    @Override
    public List<DerpibooruComment> parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        if (doc.select("div.metabar").first() == null) {
            return new ArrayList<>();
        }
        Elements commentsRaw = doc.select("article");

        int commentCount = commentsRaw.size();
        List<DerpibooruComment> commentList = new ArrayList<>(commentCount);

        for (int i = 0; i < commentCount; i++) {
            commentList.add(mCommentParser.parseResponse(commentsRaw.get(i).outerHtml()));
        }
        return commentList;
    }
}
