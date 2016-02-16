package derpibooru.derpy.server.requesters;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.storage.UserDataStorage;

public class FilterChangeRequester extends AuthenticatedRequester {
    private String mAuthenticityToken;
    private DerpibooruFilter mNewFilter;

    public FilterChangeRequester(Context context, final QueryHandler handler,
                                 DerpibooruFilter newFilter) {
        super(context, handler);
        mNewFilter = newFilter;
    }

    @Override
    protected Map<String, String> generateForm() {
        HashMap<String, String> form = new HashMap<>();
        form.put("_method", "patch");
        form.put("authenticity_token", mAuthenticityToken);
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append("filters/select");
        sb.append("?id=").append(mNewFilter.getId());
        return sb.toString();
    }

    @Override
    public void fetch() {
        fetchToken();
    }

    @Override
    protected void cacheResponse(Object parsedResponse) {
        new UserDataStorage(mContext).setCurrentFilter(mNewFilter);
    }

    @Override
    protected void onTokenFetched(String token) {
        mAuthenticityToken = token;
        executeQuery(null);
    }
}
