package derpibooru.derpy.server.requesters;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.server.QueryHandler;

public class LogoutRequester extends AuthenticatedRequester {
    private String mAuthenticityToken;

    public LogoutRequester(Context context, QueryHandler handler) {
        super(context, handler);
    }

    @Override
    protected Map<String, String> generateForm() {
        HashMap<String, String> form = new HashMap<>();
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