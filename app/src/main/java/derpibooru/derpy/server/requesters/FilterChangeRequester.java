package derpibooru.derpy.server.requesters;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.QueryHandler;

public class FilterChangeRequester extends AuthenticatedRequester<Boolean> {
    private String mAuthenticityToken;
    private DerpibooruFilter mNewFilter;

    public FilterChangeRequester(Context context, final QueryHandler<Boolean> handler,
                                 DerpibooruFilter newFilter) {
        super(context, handler);
        mNewFilter = newFilter;
    }

    @Override
    protected Map<String, String> generateForm() {
        Map<String, String> form = new HashMap<>(2);
        form.put("_method", "patch");
        form.put("authenticity_token", mAuthenticityToken);
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append("filters/current");
        sb.append("?id=").append(mNewFilter.getId());
        Log.d("auzbuzzard", String.format("FilterChangeRequester::generateUrl url: %s", sb.toString()));
        return sb.toString();
    }

    @Override
    public void fetch() {
        fetchToken();
    }

    /**
     * For filter change requests, the response code returned by server on success is 302.
     */
    @Override
    public int getSuccessResponseCode() {
        return 302;
    }

    @Override
    protected void onTokenFetched(String token) {
        Log.d("auzbuzzard", String.format("token: %s", token));
        mAuthenticityToken = token;
        executeQuery(null);
    }
}
