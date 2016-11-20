package derpibooru.derpy.server.parsers.objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.server.parsers.UserDataParser;

/**
 * Represents a page header ('.header') object, containing basic information about authenticated user.
 */
public class HeaderParserObject {
    private static final Pattern PATTERN_FILTER_NAME = Pattern.compile("^(?:Filters )(?:\\()(.*)(?:\\))$");

    private Element mHeader;
    private boolean mIsLoggedIn;

    public HeaderParserObject(Document doc) {
        mHeader = doc.select(".header").first();
        /* The quick filter switch is only present when the user is logged in */
        mIsLoggedIn = mHeader.select("form#filter-quick-form").first() != null;
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
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

    public String getAvatarUrl() {
        Element avatarImg = mHeader.select(".header__link-user img").first();
        return UserDataParser.parseAvatarUrlFromImgElement(avatarImg);
    }
}
