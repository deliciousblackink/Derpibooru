package derpibooru.derpy.server.parsers.objects;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruFilter;

/**
 * Represents a script injected by Derpibooru in every HTML page. It contains basic information
 * about the user.
 */
public class UserScriptParserObject {
    private String mHtml;

    public UserScriptParserObject(String userScriptHtml) {
        mHtml = userScriptHtml;
    }

    public String getUsername() {
        Matcher m = Pattern.compile("(?:window.booru.userName = \")(.*)(?:\";)").matcher(mHtml);
        if (m.find() && (!m.group(1).equals("null"))) {
            return m.group(1);
        }
        return "";
    }

    public String getAvatarUrl() {
        Matcher m = Pattern.compile("(?:window.booru.userAvatar = \")(.*)(?:\";)").matcher(mHtml);
        return m.find() ? getAbsoluteUrl(m.group(1)) : "";
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return String.format("https:%s", relativeUrl);
    }

    public int getFilterId() {
        Matcher m = Pattern.compile("(?:window.booru.filterID = )([\\d]*)").matcher(mHtml);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    public List<Integer> getSpoileredTagIds() {
        Matcher m = Pattern.compile("(?:window.booru.spoileredTagList = )(?:\\[)(.*)(?:\\])").matcher(mHtml);
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

    @Nullable
    public JSONArray getInteractions() throws JSONException {
        Matcher m = Pattern.compile("(?:window.booru._interactions = )(\\[.*\\])", Pattern.DOTALL).matcher(mHtml);
        m.find();
        String interactionsArray = m.group(1);
        if (!interactionsArray.equals("[]")) {
            return new JSONArray(interactionsArray);
        }
        return null;
    }
}
