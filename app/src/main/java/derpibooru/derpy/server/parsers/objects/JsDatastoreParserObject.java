package derpibooru.derpy.server.parsers.objects;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a script injected by Derpibooru in every HTML page. It contains basic information
 * about the user.
 */
public class JsDatastoreParserObject {
    private Element mJsDatastore;

    public JsDatastoreParserObject(Document doc) {
        mJsDatastore = doc.select(".js-datastore").first();
    }

    public String getUsername() {
        return mJsDatastore.attr("data-user-name");
    }

    public int getFilterId() {
        return Integer.parseInt(mJsDatastore.attr("data-filter-id"));
    }

    public List<Integer> getHiddenTagIds() throws JSONException {
        return getIntList(mJsDatastore.attr("data-hidden-tag-list"));
    }

    public List<Integer> getSpoileredTagIds() throws JSONException {
        return getIntList(mJsDatastore.attr("data-spoilered-tag-list"));
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
        String interactionsArray = mJsDatastore.attr("data-interactions");
        if (!interactionsArray.equals("[]")) {
            return new JSONArray(interactionsArray);
        }
        return new JSONArray();
    }
}
