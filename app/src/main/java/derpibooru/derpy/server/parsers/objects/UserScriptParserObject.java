package derpibooru.derpy.server.parsers.objects;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a script injected by Derpibooru in every HTML page. It contains basic information
 * about the user.
 */
public class UserScriptParserObject {
    private static final Pattern PATTERN_USERNAME = Pattern.compile("(?:window.booru.userName = \")(.*)(?:\";)");
    private static final Pattern PATTERN_AVATAR = Pattern.compile("(?:window.booru.userAvatar = \")(.*)(?:\";)");
    private static final Pattern PATTERN_FILTER_ID = Pattern.compile("(?:window.booru.filterID = )([\\d]*)");
    private static final Pattern PATTERN_HIDDEN_TAG_LIST = Pattern.compile("(?:window.booru.hiddenTagList = )(\\[.*\\])", Pattern.DOTALL);
    private static final Pattern PATTERN_SPOILERED_TAG_LIST = Pattern.compile("(?:window.booru.spoileredTagList = )(\\[.*\\])", Pattern.DOTALL);
    private static final Pattern PATTERN_IMAGE_INTERACTIONS = Pattern.compile("(?:window.booru._interactions = )(\\[.*\\])", Pattern.DOTALL);

    private String mHtml;

    public UserScriptParserObject(String userScriptHtml) {
        mHtml = userScriptHtml;
    }

    public String getUsername() {
        Matcher m = PATTERN_USERNAME.matcher(mHtml);
        if (m.find() && (!m.group(1).equals("null"))) {
            return m.group(1);
        }
        return "";
    }

    public String getAvatarUrl() {
        Matcher m = PATTERN_AVATAR.matcher(mHtml);
        return m.find() ? getAbsoluteUrl(m.group(1)) : "";
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return String.format("https:%s", relativeUrl);
    }

    public int getFilterId() {
        Matcher m = PATTERN_FILTER_ID.matcher(mHtml);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    public List<Integer> getHiddenTagIds() throws JSONException {
        Matcher m = PATTERN_HIDDEN_TAG_LIST.matcher(mHtml);
        m.find();
        return getIntList(m.group(1));
    }

    public List<Integer> getSpoileredTagIds() throws JSONException {
        Matcher m = PATTERN_SPOILERED_TAG_LIST.matcher(mHtml);
        m.find();
        return getIntList(m.group(1));
    }

    private List<Integer> getIntList(String jsonString) throws JSONException {
        if (!jsonString.isEmpty()) {
            JSONArray json = new JSONArray(jsonString);
            List<Integer> out = new ArrayList<>(json.length());
            for (int i = 0; i < json.length(); i++) {
                out.add(json.getInt(i));
            }
            return out;
        }
        return Collections.emptyList();
    }

    @NonNull
    public JSONArray getInteractions() throws JSONException {
        Matcher m = PATTERN_IMAGE_INTERACTIONS.matcher(mHtml);
        m.find();
        String interactionsArray = m.group(1);
        if (!interactionsArray.equals("[]")) {
            return new JSONArray(interactionsArray);
        }
        return new JSONArray();
    }
}
