package derpibooru.derpy.server.requesters;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.server.QueryHandler;

public class LogoutRequester extends AuthenticatedRequester<Boolean> {
    private String mAuthenticityToken;

    public LogoutRequester(Context context, QueryHandler<Boolean> handler) {
        super(context, handler);
    }

    @Override
    protected Map<String, String> generateForm() {
        Map<String, String> form = new HashMap<>(2);
        form.put("_method", "delete");
        form.put("authenticity_token", mAuthenticityToken);
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append("users/sign_out/");
        return sb.toString();
    }

    /**
     * For authentication requests, the response code returned by server on success is 302.
     */
    @Override
    public int getSuccessResponseCode() {
        return 302;
    }

    @Override
    public void fetch() {
        fetchToken();
    }

    @Override
    protected void onTokenFetched(String token) {
        mAuthenticityToken = token;
        executeQuery(null);
    }
}