package derpibooru.derpy.server.requesters;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.server.QueryHandler;

public class LoginRequester extends AuthenticatedRequester {
    private DerpibooruLoginForm mCredentials;
    private String mAuthenticityToken;

    public LoginRequester(Context context, DerpibooruLoginForm credentials, QueryHandler handler) {
        super(context, handler);
        mCredentials = credentials;
    }

    @Override
    protected Map<String, String> generateForm() {
        HashMap<String, String> form = new HashMap<>();
        form.put("utf8", "✓");
        form.put("authenticity_token", mAuthenticityToken);
        form.put("user[email]", mCredentials.getEmail());
        form.put("user[password]", mCredentials.getPassword());
        if (mCredentials.isUserToBeRemembered()) {
            form.put("user[remember_me]", "1");
        } else {
            form.put("user[remember_me]", "0");
        }
        form.put("commit", "Sign in");
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append("users/sign_in/");
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