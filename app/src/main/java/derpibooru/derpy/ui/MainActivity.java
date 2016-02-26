package derpibooru.derpy.ui;

import android.os.Bundle;
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
import derpibooru.derpy.ui.fragments.FilterListFragment;
import derpibooru.derpy.ui.fragments.HomeFragment;
import derpibooru.derpy.ui.fragments.SearchFragment;
import derpibooru.derpy.ui.fragments.UserImageListFragment;

public class MainActivity extends NavigationDrawerUserFragmentActivity {
    private List<NavigationDrawerItem> mFragmentNavigationItems;

    @Bind(R.id.fragmentLayout) FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initialize(savedInstanceState);
        initializeFragmentNavigationItems();
        navigateTo(mFragmentNavigationItems.get(0));
    }

    @Override
    protected List<NavigationDrawerItem> getFragmentNavigationItems() {
        return mFragmentNavigationItems;
    }

    @Override
    protected FrameLayout getContentLayout() {
        return mFragmentLayout;
    }

    private void initializeFragmentNavigationItems() {
        /* FIXME: keeping multiple instances of Bundle to store ints is extremely inefficient resource-wise */
        Bundle userListFaved = new Bundle();
        userListFaved.putInt("type", UserImageListProvider.UserListType.Faved.toValue());
        Bundle userListUpvoted = new Bundle();
        userListUpvoted.putInt("type", UserImageListProvider.UserListType.Upvoted.toValue());
        Bundle userListUploaded = new Bundle();
        userListUploaded.putInt("type", UserImageListProvider.UserListType.Uploaded.toValue());
        mFragmentNavigationItems = Arrays.asList(
                new NavigationDrawerItem(R.id.navigationHome, HomeFragment.class),
                new NavigationDrawerItem(R.id.navigationSearch, SearchFragment.class),
                new NavigationDrawerItem(R.id.navigationFilters, FilterListFragment.class),
                new NavigationDrawerItem(R.id.navigationFaves, UserImageListFragment.class, userListFaved),
                new NavigationDrawerItem(R.id.navigationUpvoted, UserImageListFragment.class, userListUpvoted),
                new NavigationDrawerItem(R.id.navigationUploaded, UserImageListFragment.class, userListUploaded)
        );
    }

    @Override
    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment f = super.getFragmentInstance(fragmentMenuItem);
        if (f instanceof FilterListFragment) {
            ((FilterListFragment) f).setOnFilterChangeListener(new FilterListFragment.OnFilterChangeListener() {
                @Override
                public void onFilterChanged() {
                    MainActivity.super.refreshUser();
                }
            });
        }
        return f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
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
