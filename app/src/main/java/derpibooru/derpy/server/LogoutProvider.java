package derpibooru.derpy.server;

import android.content.Context;

import java.util.HashMap;

public class LogoutProvider extends Provider {
    private String mAuthenticityToken;

    public LogoutProvider(Context context, String authenticityToken, ProviderRequestHandler handler) {
        super(context, handler);
        mAuthenticityToken = authenticityToken;
    }

    private HashMap<String, String> generateLogoutForm() {
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
        executeQueryWithForm(null, generateLogoutForm());
    }
}