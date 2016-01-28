package derpibooru.derpy.server;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.server.parsers.ServerResponseParser;

class Authenticator {
    private DerpibooruLoginForm mSignInForm;
    private Context mContext;

    private DataProviderRequestHandler mHandler;

    public Authenticator(Context context, DataProviderRequestHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void attemptLogin(DerpibooruLoginForm form) {
        mSignInForm = form;
        LoginAuthenticityTokenProvider authTokenProvider =
                new LoginAuthenticityTokenProvider(mContext, new DataProviderRequestHandler() {
                    @Override
                    public void onDataFetched(Object result) {
                        loginWithToken((String) result);
                    }

                    @Override
                    public void onDataRequestFailed() {

                    }
                });
        authTokenProvider.fetch();
    }

    public void attemptLogout() {
        AuthenticityTokenProvider authTokenProvider =
                new AuthenticityTokenProvider(mContext, new DataProviderRequestHandler() {
                    @Override
                    public void onDataFetched(Object result) {
                        logoutWithToken((String) result);
                    }

                    @Override
                    public void onDataRequestFailed() {

                    }
                });
        authTokenProvider.fetch();
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

    private class AuthenticityTokenProvider extends DataProvider {
        public AuthenticityTokenProvider(Context context,
                                         DataProviderRequestHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN);
            return sb.toString();
        }

        @Override
        public void fetch() {
            super.executeQuery(generateUrl(), new AuthenticityTokenParser());
        }
    }

    private class AuthenticityTokenParser implements ServerResponseParser {
        @Override
        public Object parseResponse(String rawResponse) throws Exception {
            Document doc = Jsoup.parse(rawResponse);
            Elements metaHeaders = doc.select("head").first().select("meta");
            for (Element header : metaHeaders) {
                if (header.attr("name").equals("csrf-token")) {
                    return header.attr("content");
                }
            }
            return null;
        }
    }

    private class LoginProvider extends DataProvider {
        private HashMap<String, String> mForm;

        public LoginProvider(Context context,
                             DataProviderRequestHandler handler,
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

    private class LoginAuthenticityTokenProvider extends AuthenticityTokenProvider {
        public LoginAuthenticityTokenProvider(Context context,
                                         DataProviderRequestHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN).append("users/sign_in/");
            return sb.toString();
        }
    }

    private class LogoutProvider extends LoginProvider {
        public LogoutProvider(Context context,
                              DataProviderRequestHandler handler,
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
