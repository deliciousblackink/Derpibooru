package derpibooru.derpy.server;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruLoginForm;

class Authenticator {
    private Context mContext;

    private ProviderRequestHandler mHandler;

    public Authenticator(Context context, ProviderRequestHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void attemptLogin(final DerpibooruLoginForm credentials) {
        new AuthenticityToken(mContext, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                String token = (String) result;
                new LoginProvider(mContext, token, credentials, mHandler).fetch();
            }

            @Override
            public void onRequestFailed() { }
        }, AuthenticityToken.TokenAction.Login).fetch();
    }

    public void attemptLogout() {
        new AuthenticityToken(mContext, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                String token = (String) result;
                new LogoutProvider(mContext, token, mHandler).fetch();
            }

            @Override
            public void onRequestFailed() { }
        }, AuthenticityToken.TokenAction.General).fetch();
    }
}
