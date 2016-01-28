package derpibooru.derpy.server;

import android.content.Context;
import android.support.annotation.Nullable;

import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.UserDataParser;
import derpibooru.derpy.storage.UserDataStorage;

public class User {
    private Context mContext;
    private UserActionPerformedHandler mHandler;

    private UserDataStorage mUserDataStorage;
    private UserDataProvider mUserProvider;
    private Authenticator mAuth;

    private AuthenticatorAction mCurrentAction;

    public User(Context context, UserActionPerformedHandler handler) {
        mContext = context;
        mHandler = handler;
        mAuth = new Authenticator(context, new DataProviderRequestHandler() {
            @Override
            public void onDataFetched(Object result) {
                onAuthenticatorResponseReceived((boolean) result);
            }

            @Override
            public void onDataRequestFailed() {
                mHandler.onNetworkError();
            }
        });
        mUserDataStorage = new UserDataStorage(context);
        mUserProvider = new UserDataProvider(context, new DataProviderRequestHandler() {
            @Override
            public void onDataFetched(Object result) {
                onUserDataFetched((DerpibooruUser) result);
            }

            @Override
            public void onDataRequestFailed() {
                mHandler.onNetworkError();
            }
        });
    }

    public boolean isLoggedIn() {
        return mUserDataStorage.getUserData().isLoggedIn();
    }

    public void login(DerpibooruLoginForm form) {
        mCurrentAction = AuthenticatorAction.Login;
        mAuth.attemptLogin(form);
    }

    public void logout() {
        mCurrentAction = AuthenticatorAction.Logout;
        mAuth.attemptLogout();
    }

    @Nullable
    public void fetchUserData() {
        DerpibooruUser cached = mUserDataStorage.getUserData();
        if (cached == null) {
            mUserProvider.fetch();
        } else {
            mHandler.onUserDataObtained(cached);
        }
    }

    public void refreshUserData() {
        mUserDataStorage.clearUserData();
        mUserProvider.fetch();
    }

    private void onAuthenticatorResponseReceived(boolean result) {
        switch (mCurrentAction) {
            case Login:
                if (result) {
                    mUserProvider.fetch();
                } else {
                    mHandler.onFailedLogin();
                }
                break;
            case Logout:
                if (result) {
                    mUserProvider.fetch();
                } else {
                    mHandler.onFailedLogout();
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

    public interface UserActionPerformedHandler {
        void onUserDataObtained(DerpibooruUser userData);
        void onFailedLogin();
        void onFailedLogout();
        void onNetworkError();
    }

    private class UserDataProvider extends DataProvider {
        public UserDataProvider(Context context, DataProviderRequestHandler handler) {
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
