package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AuthenticityTokenParser implements ServerResponseParser<String> {
    @Override
    public String parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        Elements metaHeaders = doc.select("head").first().select("meta");
        for (Element header : metaHeaders) {
            if (header.attr("name").equals("csrf-token")) {
                return header.attr("content");
            }
        }
        return null;
    }
}
