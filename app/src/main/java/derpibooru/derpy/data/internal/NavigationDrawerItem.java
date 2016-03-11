package derpibooru.derpy.data.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.common.base.Objects;

public class NavigationDrawerItem {
    private final int mNavigationViewItemId;
    private final Class<? extends Fragment> mFragmentClass;
    private Bundle mFragmentArguments;

    public NavigationDrawerItem(int navigationViewItemId, Class<? extends Fragment> fragmentClass) {
        mNavigationViewItemId = navigationViewItemId;
        mFragmentClass = fragmentClass;
    }

    public NavigationDrawerItem(int navigationViewItemId, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        mNavigationViewItemId = navigationViewItemId;
        mFragmentClass = fragmentClass;
        mFragmentArguments = fragmentArguments;
    }

    NavigationDrawerItem(NavigationDrawerItem from) {
        mNavigationViewItemId = from.getNavigationViewItemId();
        mFragmentClass = from.getFragmentClass();
        mFragmentArguments = from.getFragmentArguments();
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

    public String getUniqueTag() {
        return Integer.toString(Objects.hashCode(mNavigationViewItemId, mFragmentClass));
    }
}
