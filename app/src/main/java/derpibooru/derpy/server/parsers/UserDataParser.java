package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;

public class UserDataParser implements ServerResponseParser {
    @Override
    public Object parseResponse(String rawResponse) throws Exception {
        String userName = (String)
                new UserNameParser().parseResponse(rawResponse);
        DerpibooruFilter currentFilter = (DerpibooruFilter)
                new CurrentFilterParser().parseResponse(rawResponse);

        /* TODO: parse avatars */

        return new DerpibooruUser(userName)
                .setCurrentFilter(currentFilter);
    }

    public class CurrentFilterParser implements ServerResponseParser {
        private boolean mIsLoggedIn = false;

        @Override
        public Object parseResponse(String rawResponse) throws Exception {
            Document doc = Jsoup.parse(rawResponse);
            mIsLoggedIn = isLoggedIn(doc);

            Element derpibooruUserScript;
            if (mIsLoggedIn) {
                derpibooruUserScript = doc.select("script").get(doc.select("script").size() - 2);
            } else {
                derpibooruUserScript = doc.select("script").last();
            }
            int id = getFilterId(derpibooruUserScript.html());
            String name = getFilterName(doc);
            List<Integer> spoileredTags = getSpoileredTags(derpibooruUserScript.html());
            return new DerpibooruFilter(id, name, spoileredTags);
        }

        private boolean isLoggedIn(Document doc) {
            /* a quick filter switch is only present when the user is
             * logged in */
            return doc.select("form#filter-quick-form").first() != null;
        }

        private int getFilterId(String scriptHtml) {
            Matcher m = Pattern.compile("(?:window.booru.filterID = )([\\d]*)")
                    .matcher(scriptHtml);
            return m.find() ? Integer.parseInt(m.group(1)) : 0;
        }

        private String getFilterName(Document doc) {
            if (mIsLoggedIn) {
                return doc.select("form#filter-quick-form").first()
                        .select("option").first().text();
            } else {
                String filter = doc.select("div.userbox").first()
                        .select("a.hide-mobile").text();
                Matcher m = Pattern.compile("^(?:Filters )(?:\\()(.*)(?:\\))$")
                        .matcher(filter);
                m.find();
                return m.group(1);
            }
        }

        private List<Integer> getSpoileredTags(String scriptHtml) {
            Matcher m = Pattern.compile("(?:window.booru.spoileredTagList = )(?:\\[)(.*)(?:\\])")
                    .matcher(scriptHtml);
            m.find();
            String tagIdsArray = m.group(1);
            if (!tagIdsArray.equals("")) {
                List<String> stringIds = new ArrayList<>(Arrays.asList(tagIdsArray.split(",")));
                List<Integer> intIds = new ArrayList<>();
                for (String stringId : stringIds) {
                    intIds.add(Integer.parseInt(stringId));
                }
                return intIds;
            }
            return Collections.emptyList();
        }
    }

    public class UserNameParser implements ServerResponseParser {
        @Override
        public Object parseResponse(String rawResponse) throws Exception {
            Document doc = Jsoup.parse(rawResponse);
            Element profileLink = doc.select("div.userbox").first()
                    .select("a.hide-desktop-t").first();

            if (profileLink != null) {
                String profileHref = profileLink.attr("href");

                Matcher m = Pattern.compile("(?:[/]profiles[/])(.*)")
                        .matcher(profileHref);
                return m.find() ? m.group(1) : "";
            }
            return "";
        }
    }
}
