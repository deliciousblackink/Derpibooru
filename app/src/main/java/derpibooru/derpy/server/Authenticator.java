package derpibooru.derpy.server;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruSignInForm;
import derpibooru.derpy.server.parsers.ServerResponseParser;

public class Authenticator {
    private DerpibooruSignInForm mSignInForm;
    private Context mContext;

    private DataProviderRequestHandler mHandler;

    public Authenticator(Context context, DerpibooruSignInForm form,
                         DataProviderRequestHandler handler) {
        mSignInForm = form;
        mContext = context;
        mHandler = handler;
    }

    public void attemptAuth() {
        AuthenticityTokenProvider authTokenProvider =
                new AuthenticityTokenProvider(mContext, new DataProviderRequestHandler() {
                    @Override
                    public void onDataFetched(Object result) {
                        onAuthTokenFetched((String) result);
                    }

                    @Override
                    public void onDataRequestFailed() {

                    }
                });
        authTokenProvider.fetch();
    }

    private void onAuthTokenFetched(String token) {
        HashMap<String, String> form = buildForm(token);
        LoginProvider provider = new LoginProvider(mContext, mHandler, form);
        provider.fetch();
    }

    private HashMap<String, String> buildForm(String token) {
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

    private class AuthenticityTokenProvider extends DataProvider {
        public AuthenticityTokenProvider(Context context,
                                         DataProviderRequestHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN).append("users/sign_in/");
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
                    new AsynchronousPostRequest(mContext, parser, url, mForm,
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
}
