package derpibooru.derpy.data.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class NavigationDrawerItem {
    private int mNavigationViewItemId;
    private String mToolbarTitle;
    private Class<? extends Fragment> mFragmentClass;
    private Bundle mFragmentArguments;

    public NavigationDrawerItem(int navigationViewItemId, String toolbarTitle, Class<? extends Fragment> fragmentClass) {
        mNavigationViewItemId = navigationViewItemId;
        mToolbarTitle = toolbarTitle;
        mFragmentClass = fragmentClass;
    }

    public NavigationDrawerItem(int navigationViewItemId, String toolbarTitle,
                                Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        mNavigationViewItemId = navigationViewItemId;
        mToolbarTitle = toolbarTitle;
        mFragmentClass = fragmentClass;
        mFragmentArguments = fragmentArguments;
    }

    public int getNavigationViewItemId() {
        return mNavigationViewItemId;
    }

    public String getToolbarTitle() {
        return mToolbarTitle;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    @Nullable
    public Bundle getFragmentArguments() {
        return mFragmentArguments;
    }
}
