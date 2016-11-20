package derpibooru.derpy.server.parsers;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.objects.JsDatastoreParserObject;
import derpibooru.derpy.server.parsers.objects.HeaderParserObject;

public class UserDataParser implements ServerResponseParser<DerpibooruUser> {
    public static final String DEFAULT_AVATAR_SRC = "https://derpicdn.net/img/view/2016/11/8/1291192.svg";

    private HeaderParserObject mHeader;
    private JsDatastoreParserObject mJsDatastore;

    @Override
    public DerpibooruUser parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        mHeader = new HeaderParserObject(doc);
        mJsDatastore = new JsDatastoreParserObject(doc);
        return new DerpibooruUser(mJsDatastore.getUsername(), mHeader.getAvatarUrl())
                .setCurrentFilter(parseCurrentFilter());
    }

    private DerpibooruFilter parseCurrentFilter() throws JSONException {
        return new DerpibooruFilter(mJsDatastore.getFilterId(), mHeader.getFilterName(),
                                    mJsDatastore.getHiddenTagIds(), mJsDatastore.getSpoileredTagIds());
    }

    public static String parseAvatarUrlFromImgElement(@Nullable Element imgElement) {
        if (imgElement == null) {
            return DEFAULT_AVATAR_SRC;
        } else {
            return String.format("https:%s", imgElement.attr("src"));
        }
    }
}
