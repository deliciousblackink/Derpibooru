package derpibooru.derpy.data.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class NavigationDrawerItem {
    private int mNavigationViewItemId;
    private Class<? extends Fragment> mFragmentClass;
    private Bundle mFragmentArguments;

    public NavigationDrawerItem(int navigationViewItemId, Class<? extends Fragment> fragmentClass) {
        mNavigationViewItemId = navigationViewItemId;
        mFragmentClass = fragmentClass;
    }

    public NavigationDrawerItem(int navigationViewItemId, Class<? extends Fragment> fragmentClass,
                                Bundle fragmentArguments) {
        mNavigationViewItemId = navigationViewItemId;
        mFragmentClass = fragmentClass;
        mFragmentArguments = fragmentArguments;
    }

    public int getNavigationViewItemId() {
        return mNavigationViewItemId;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    @Nullable
    public Bundle getFragmentArguments() {
        return mFragmentArguments;
    }
}
