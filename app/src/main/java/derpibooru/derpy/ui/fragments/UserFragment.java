package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.MainActivity;

public abstract class UserFragment extends Fragment {
    private DerpibooruUser mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelableArrayList(MainActivity.EXTRAS_USER) != null)) {
            mUserData = savedInstanceState.getParcelable(MainActivity.EXTRAS_USER);
        } else if (getArguments().getParcelable(MainActivity.EXTRAS_USER) != null) {
            mUserData = getArguments().getParcelable(MainActivity.EXTRAS_USER);
        } else {
            throw new IllegalStateException("UserFragment didn't receive DerpibooruUser as an argument");
        }
    }

    protected abstract void onUserRefreshed(DerpibooruUser user);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.EXTRAS_USER, mUserData);
    }

    public void setRefreshedUserData(DerpibooruUser newUserData) {
        mUserData = newUserData;
        onUserRefreshed(mUserData);
    }

    protected DerpibooruUser getUser() {
        return mUserData;
    }
}
