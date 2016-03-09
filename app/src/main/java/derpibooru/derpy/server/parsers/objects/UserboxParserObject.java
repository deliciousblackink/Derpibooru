package derpibooru.derpy.server.parsers.objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a page header ('div.userbox') object. On the website, it is found in the top right corner of
 * a served HTML page and contains basic information about the user.
 */
public class UserboxParserObject {
    private Element mUserbox;

    public UserboxParserObject(String userboxHtml) {
        mUserbox = Jsoup.parse(userboxHtml);
    }

    public boolean isLoggedIn() {
        /* a quick filter switch is only present when the user is logged in */
        return mUserbox.select("form#filter-quick-form").first() != null;
    }

    public String getFilterName() {
        if (isLoggedIn()) {
            return mUserbox.select("form#filter-quick-form").first()
                    .select("option[selected]").first().text();
        } else {
            String filter = mUserbox.select("a.hide-mobile").text();
            Matcher m = Pattern.compile("^(?:Filters )(?:\\()(.*)(?:\\))$")
                    .matcher(filter);
            m.find();
            return m.group(1);
        }
    }
}
