package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ApiKeyParser implements ServerResponseParser<String> {
    @Override
    public String parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        return doc.select("p").get(1).select("strong").first().text();
    }
}
