package derpibooru.derpy.server;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.server.parsers.ServerResponseParser;

class Authenticator {
    private DerpibooruLoginForm mSignInForm;
    private Context mContext;

    private ProviderRequestHandler mHandler;

    public Authenticator(Context context, ProviderRequestHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void attemptLogin(DerpibooruLoginForm form) {
        mSignInForm = form;
        AuthenticityToken authToken =
                new AuthenticityToken(mContext, new ProviderRequestHandler() {
                    @Override
                    public void onRequestCompleted(Object result) {
                        loginWithToken((String) result);
                    }

                    @Override
                    public void onRequestFailed() { }
                }, AuthenticityToken.TokenAction.Login);
        authToken.fetch();
    }

    public void attemptLogout() {
        AuthenticityToken authToken =
                new AuthenticityToken(mContext, new ProviderRequestHandler() {
                    @Override
                    public void onRequestCompleted(Object result) {
                        logoutWithToken((String) result);
                    }

                    @Override
                    public void onRequestFailed() { }
                }, AuthenticityToken.TokenAction.General);
        authToken.fetch();
    }

    private void loginWithToken(String token) {
        HashMap<String, String> form = buildLoginForm(token);
        LoginProvider provider = new LoginProvider(mContext, mHandler, form);
        provider.fetch();
    }

    private void logoutWithToken(String token) {
        HashMap<String, String> form = buildLogoutForm(token);
        LogoutProvider provider = new LogoutProvider(mContext, mHandler, form);
        provider.fetch();
    }

    private HashMap<String, String> buildLoginForm(String token) {
        HashMap<String, String> form = new HashMap<>();
        form.put("utf8", "✓");
        form.put("authenticity_token", token);
        form.put("user[email]", mSignInForm.getEmail());
        form.put("user[password]", mSignInForm.getPassword());
        if (mSignInForm.isRememberUser()) {
            form.put("user[remember_me]", "1");
        } else {
            form.put("user[remember_me]", "0");
        }
        form.put("commit", "Sign in");
        return form;
    }

    private HashMap<String, String> buildLogoutForm(String token) {
        HashMap<String, String> form = new HashMap<>();
        form.put("_method", "delete");
        form.put("authenticity_token", token);
        return form;
    }

    private class LoginProvider extends Provider {
        private HashMap<String, String> mForm;

        public LoginProvider(Context context,
                             ProviderRequestHandler handler,
                             HashMap<String, String> form) {
            super(context, handler);
            mForm = form;
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN).append("users/sign_in/");
            return sb.toString();
        }

        @Override
        public void fetch() {
            executeQuery(generateUrl(), null);
        }

        @Override
        protected void executeQuery(String url, ServerResponseParser parser) {
            Handler threadHandler = new Handler();
            AsynchronousPostRequest requestThread =
                    new AsynchronousPostRequest(mContext, url, mForm,
                                                new AsynchronousRequest.RequestHandler() {
                                                    Handler uiThread = new Handler(Looper.getMainLooper());

                                                    @Override
                                                    public void onRequestCompleted(Object parsedResponse) {
                                                        uiThread.post(new UiThreadMessageSender(parsedResponse, false));
                                                    }

                                                    @Override
                                                    public void onRequestFailed() {
                                                        uiThread.post(new UiThreadMessageSender(null, true));
                                                    }
                                                });
            threadHandler.post(requestThread);
        }
    }

    private class LogoutProvider extends LoginProvider {
        public LogoutProvider(Context context,
                              ProviderRequestHandler handler,
                              HashMap<String, String> form) {
            super(context, handler, form);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN).append("users/sign_out/");
            return sb.toString();
        }
    }
}
