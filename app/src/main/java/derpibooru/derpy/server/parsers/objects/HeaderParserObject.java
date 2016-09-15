package derpibooru.derpy.server.parsers.objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a page header ('.header') object, containing basic information about authenticated user.
 */
public class HeaderParserObject {
    private static final Pattern PATTERN_FILTER_NAME = Pattern.compile("^(?:Filters )(?:\\()(.*)(?:\\))$");

    private Element mHeader;

    public HeaderParserObject(String headerHtml) {
        mHeader = Jsoup.parse(headerHtml);
    }

    public boolean isLoggedIn() {
        /* a quick filter switch is only present when the user is logged in */
        return mHeader.select("form#filter-quick-form").first() != null;
    }

    public String getFilterName() {
        if (isLoggedIn()) {
            return mHeader.select("form#filter-quick-form").first()
                    .select("option[selected]").first().text();
        } else {
            String filter = mHeader.select("a[href=\"/filters\"]").text();
            Matcher m = PATTERN_FILTER_NAME.matcher(filter);
            m.find();
            return m.group(1);
        }
    }
}
