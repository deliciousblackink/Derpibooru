package derpibooru.derpy.server;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import derpibooru.derpy.server.parsers.ServerResponseParser;

class AuthenticityToken extends Provider {
    private TokenAction mAction;

    public AuthenticityToken(Context context, ProviderRequestHandler handler,
                             TokenAction action) {
        super(context, handler);
        mAction = action;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append(mAction.url());
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(generateUrl(), new AuthenticityTokenParser());
    }

    private class AuthenticityTokenParser implements ServerResponseParser {
        @Override
        public Object parseResponse(String rawResponse) throws Exception {
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

    public enum TokenAction {
        Login("users/sign_in/"),
        ChangeFilter("filters/"),
        General("");

        private final String url;

        TokenAction(final String text) {
            this.url = text;
        }

        public String url() {
            return url;
        }
    }
}