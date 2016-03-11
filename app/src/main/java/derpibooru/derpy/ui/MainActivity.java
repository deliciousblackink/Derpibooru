package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.server.providers.UserImageListProvider;
import derpibooru.derpy.ui.fragments.BrowseFragment;
import derpibooru.derpy.ui.fragments.BrowseImageListFragment;
import derpibooru.derpy.ui.fragments.FilterListFragment;
import derpibooru.derpy.ui.fragments.UserImageListFragment;

public class MainActivity extends NavigationDrawerUserFragmentActivity {
    private List<NavigationDrawerItem> mFragmentNavigationItems;

    @Bind(R.id.fragmentLayout) FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFragmentNavigationItems();
        super.initialize(savedInstanceState);
        if (getSupportFragmentManager().getFragments() == null) {
            navigateTo(getFragmentNavigationItems().get(0));
        }
        setCallbackHandlersFor(super.getCurrentFragment());
    }

    @NonNull
    @Override
    protected List<NavigationDrawerItem> getFragmentNavigationItems() {
        return mFragmentNavigationItems;
    }

    @Override
    protected FrameLayout getContentLayout() {
        return mFragmentLayout;
    }

    private void initializeFragmentNavigationItems() {
        /* FIXME: keeping multiple instances of Bundle to store ints is inefficient resource-wise */
        Bundle watched = new Bundle();
        watched.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserWatched.toValue());
        Bundle faved = new Bundle();
        faved.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserFaved.toValue());
        Bundle upvoted = new Bundle();
        upvoted.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserUpvoted.toValue());
        Bundle uploaded = new Bundle();
        uploaded.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserUploaded.toValue());
        mFragmentNavigationItems = Arrays.asList(
                new NavigationDrawerItem(
                        R.id.navigationBrowse, getString(R.string.fragment_home), BrowseFragment.class),
                new NavigationDrawerItem(
                        R.id.navigationFilters, getString(R.string.fragment_filters), FilterListFragment.class),
                new NavigationDrawerItem(
                        R.id.naviationWatched, getString(R.string.fragment_user_list_watched), BrowseFragment.class, watched),
                new NavigationDrawerItem(
                        R.id.navigationFaves, getString(R.string.fragment_user_list_faved), BrowseFragment.class, faved),
                new NavigationDrawerItem(
                        R.id.navigationUpvoted, getString(R.string.fragment_user_list_upvoted), BrowseFragment.class, upvoted),
                new NavigationDrawerItem(
                        R.id.navigationUploaded, getString(R.string.fragment_user_list_uploaded), BrowseFragment.class, uploaded)
        );
    }

    private void setCallbackHandlersFor(Fragment fragment) {
        if (fragment instanceof FilterListFragment) {
            ((FilterListFragment) fragment).setOnFilterChangeListener(new FilterListFragment.OnFilterChangeListener() {
                @Override
                public void onFilterChanged() {
                    MainActivity.super.refreshUser();
                }
            });
        }
    }

    @Override
    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment fragment = super.getFragmentInstance(fragmentMenuItem);
        setCallbackHandlersFor(fragment);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        /* https://code.google.com/p/android/issues/detail?id=40323 */
        if (!((getCurrentFragment() instanceof BrowseFragment)
                && ((BrowseFragment) getCurrentFragment()).popChildFragmentManagerBackstack())) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionRandomImage:
                /* TODO: random image */
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
