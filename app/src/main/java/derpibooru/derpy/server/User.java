package derpibooru.derpy.server;

import android.content.Context;
import android.support.annotation.Nullable;

import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.UserDataParser;
import derpibooru.derpy.storage.UserDataStorage;

public class User {
    private UserRequestHandler mHandler;
    private UserDataStorage mUserDataStorage;
    private UserDataProvider mUserProvider;
    private Authenticator mAuth;

    private AuthenticatorAction mCurrentAction;
    private AuthenticationRequestHandler mAuthHandler;

    public User(Context context, UserRequestHandler handler) {
        mHandler = handler;
        initUserProvider(context);
    }

    public User(Context context, UserRequestHandler handler,
                AuthenticationRequestHandler authHandler) {
        mHandler = handler;
        mAuthHandler = authHandler;
        mAuth = new Authenticator(context, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                onAuthenticatorResponseReceived((boolean) result);
            }

            @Override
            public void onRequestFailed() {
                mHandler.onNetworkError();
            }
        });
        initUserProvider(context);
    }

    private void initUserProvider(Context context) {
        mUserDataStorage = new UserDataStorage(context);
        mUserProvider = new UserDataProvider(context, new ProviderRequestHandler() {
            @Override
            public void onRequestCompleted(Object result) {
                onUserDataFetched((DerpibooruUser) result);
            }

            @Override
            public void onRequestFailed() {
                mHandler.onNetworkError();
            }
        });
    }

    public boolean isLoggedIn() {
        return mUserDataStorage.getUserData().isLoggedIn();
    }

    public void login(DerpibooruLoginForm form) {
        if (mAuthHandler != null) {
            mCurrentAction = AuthenticatorAction.Login;
            mAuth.attemptLogin(form);
        }
    }

    public void logout() {
        if (mAuthHandler != null) {
            mCurrentAction = AuthenticatorAction.Logout;
            mAuth.attemptLogout();
        }
    }

    public void fetchUserData() {
        DerpibooruUser cached = mUserDataStorage.getUserData();
        if (cached == null) {
            mUserProvider.fetch();
        } else {
            mHandler.onUserDataObtained(cached);
        }
    }

    public void refreshUserData() {
        mUserProvider.fetch();
    }

    private void onAuthenticatorResponseReceived(boolean result) {
        switch (mCurrentAction) {
            case Login:
                if (result) {
                    mUserProvider.fetch();
                } else {
                    mAuthHandler.onFailedLogin();
                }
                break;
            case Logout:
                if (result) {
                    mUserProvider.fetch();
                } else {
                    mAuthHandler.onFailedLogout();
                }
                break;
        }
    }

    private void onUserDataFetched(DerpibooruUser userData) {
        mUserDataStorage.setUserData(userData);
        mHandler.onUserDataObtained(userData);
    }

    private enum AuthenticatorAction {
        Login,
        Logout
    }

    public interface UserRequestHandler {
        void onUserDataObtained(DerpibooruUser userData);
        void onNetworkError();
    }

    public interface AuthenticationRequestHandler {
        void onFailedLogin();
        void onFailedLogout();
    }

    private class UserDataProvider extends Provider {
        public UserDataProvider(Context context, ProviderRequestHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            /* TODO: there is probably a better url to find user data at */
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN);
            sb.append("images?perpage=1");
            return sb.toString();
        }

        @Override
        public void fetch() {
            super.executeQuery(generateUrl(), new UserDataParser());
        }
    }
}
