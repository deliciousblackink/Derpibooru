package derpibooru.derpy.server;

import android.content.Context;
import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruLoginForm;

public class LoginProvider extends Provider {
    private DerpibooruLoginForm mCredentials;
    private String mAuthenticityToken;

    public LoginProvider(Context context, String authenticityToken, DerpibooruLoginForm credentials,
                         ProviderRequestHandler handler) {
        super(context, handler);
        mCredentials = credentials;
        mAuthenticityToken = authenticityToken;
    }

    private HashMap<String, String> generateLoginForm() {
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
        executeQueryWithForm(null, generateLoginForm());
    }
}