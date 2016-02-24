package derpibooru.derpy;

import android.content.Context;
import android.support.annotation.Nullable;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.Provider;
import derpibooru.derpy.server.providers.UserDataProvider;

public class User {
    private UserDataProvider mUserProvider;
    private DerpibooruUser mUser;

    private OnUserRefreshListener mRefreshListener;
    private Context mContext;

    public User(Context context) {
        mContext = context;
        mUserProvider = new UserDataProvider(context, new UserQueryHandler());
    }

    public User(Context context, DerpibooruUser user) {
        this(context);
        mUser = user;
    }

    public void setOnUserRefreshListener(OnUserRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void refresh() {
        mUserProvider.refreshUserData();
    }

    public boolean isLoggedIn() {
        return (mUser != null) && (mUser.isLoggedIn());
    }

    public DerpibooruUser getUser() throws IllegalStateException {
        if (mUser == null) {
            throw new IllegalStateException("User data has not been fetched from server yet. Ensure a call to the 'refresh()' method was made, or an exisiting object was passed on initialization");
        }
        return mUser;
    }

    public DerpibooruFilter getCurrentFilter() throws IllegalStateException {
        return getUser().getCurrentFilter();
    }

    public interface OnUserRefreshListener {
        void onUserRefreshed(DerpibooruUser user);
    }

    private class UserQueryHandler implements QueryHandler<DerpibooruUser> {
        @Override
        public void onQueryExecuted(DerpibooruUser result) {
            if (mRefreshListener != null) {
                mUser = result;
                mRefreshListener.onUserRefreshed(result);
            }
        }

        @Override
        public void onQueryFailed() {

        }
    }
}
