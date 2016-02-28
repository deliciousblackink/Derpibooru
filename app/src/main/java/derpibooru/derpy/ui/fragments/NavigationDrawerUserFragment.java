package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.MainActivity;

public abstract class NavigationDrawerUserFragment extends Fragment {
    private DerpibooruUser mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelable(MainActivity.EXTRAS_USER) != null)) {
            mUserData = savedInstanceState.getParcelable(MainActivity.EXTRAS_USER);
        } else if ((getArguments() != null)
                && (getArguments().getParcelable(MainActivity.EXTRAS_USER) != null)) {
            mUserData = getArguments().getParcelable(MainActivity.EXTRAS_USER);
        } else {
            throw new IllegalStateException("UserFragment didn't receive DerpibooruUser as an argument");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.EXTRAS_USER, mUserData);
    }

    /**
     * Triggered on user data refresh. May indicate filter or authentication state change.
     * At this point, the getUser() method returns the old user data, so the new data can be compared against that.
     * @param user updated user data
     */
    protected abstract void onUserRefreshed(DerpibooruUser user);

    /**
     * Updates the fragment with new user data and triggers the corresponding UI change.
     * @param newUserData updated user data
     */
    public void setRefreshedUserData(DerpibooruUser newUserData) {
        onUserRefreshed(newUserData);
        mUserData = newUserData;
    }

    /**
     * Returns user data. When called from the onUserRefreshed(DerpibooruUser) method, returns
     * the old data (obtained prior to refresh).
     */
    protected DerpibooruUser getUser() {
        return mUserData;
    }
}
