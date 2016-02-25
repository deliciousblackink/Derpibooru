package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import derpibooru.derpy.R;
import derpibooru.derpy.UserManager;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.UserImageListProvider;
import derpibooru.derpy.server.requesters.LogoutRequester;
import derpibooru.derpy.ui.fragments.FilterListFragment;
import derpibooru.derpy.ui.fragments.HomeFragment;
import derpibooru.derpy.ui.fragments.SearchFragment;
import derpibooru.derpy.ui.fragments.UserFragment;
import derpibooru.derpy.ui.fragments.UserImageListFragment;
import derpibooru.derpy.ui.utils.NavigationDrawerUserPresenter;

public class MainActivity extends NavigationDrawerFragmentActivity {
    public static final String EXTRAS_USER = "derpibooru.derpy.DerpibooruUser";
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;

    private List<NavigationDrawerItem> mFragmentNavigationItems;
    private NavigationDrawerUserPresenter mUserPresenter;

    @Bind(R.id.fragmentLayout) FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initializeNavigationDrawer();
        initializeUser(savedInstanceState);
        initializeFragmentNavigationItems();
        navigateTo(mFragmentNavigationItems.get(0));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRAS_USER, mUserPresenter.getUser());
    }

    @Override
    protected List<NavigationDrawerItem> getFragmentNavigationItems() {
        return mFragmentNavigationItems;
    }

    @Override
    protected FrameLayout getContentLayout() {
        return mFragmentLayout;
    }

    @Override
    protected boolean onNavigationItemSelected(MenuItem item) {
        return isAuthenticationActionSelected(item.getItemId())
                || super.onNavigationItemSelected(item);
    }

    private void initializeUser(Bundle savedInstanceState) {
        DerpibooruUser user;
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelable(EXTRAS_USER) != null)) {
            user = savedInstanceState.getParcelable(EXTRAS_USER);
        } else if (getIntent().getParcelableExtra(EXTRAS_USER) != null) {
            user = getIntent().getParcelableExtra(EXTRAS_USER);
        } else {
            throw new IllegalStateException("MainActivity didn't receive DerpibooruUser with the Intent");
        }
        mUserPresenter = new NavigationDrawerUserPresenter(this, user) {
            @Override
            protected void onUserRefreshed(DerpibooruUser user) {
                setActiveMenuItem();
                if (getCurrentFragment() instanceof UserFragment) {
                    ((UserFragment) getCurrentFragment()).setRefreshedUserData(user);
                }
            }
        };
        mUserPresenter.initializeWithView(mNavigationView);
        setActiveMenuItem();
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

    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment f = fragmentMenuItem.getFragmentClass().newInstance();
        f.setArguments(new Bundle());
        if (fragmentMenuItem.getFragmentArguments() != null) {
            f.setArguments(fragmentMenuItem.getFragmentArguments());
        }
        if (f instanceof UserFragment) {
            f.getArguments().putParcelable(EXTRAS_USER, mUserPresenter.getUser());
        }
        if (f instanceof FilterListFragment) {
            ((FilterListFragment) f).setOnFilterChangeListener(new FilterListFragment.OnFilterChangeListener() {
                @Override
                public void onFilterChanged() {
                    mUserPresenter.refreshUser();
                }
            });
        }
        return f;
    }

    private boolean isAuthenticationActionSelected(int itemId) {
        if (itemId == R.id.navigationLogin) {
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_ACTIVITY_REQUEST_CODE);
            getNavigationDrawerLayout().closeDrawer();
            return true;
        } else if (itemId == R.id.navigationLogout) {
            logout();
            return true;
        }
        return false;
    }

    private void logout() {
        new LogoutRequester(this, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                /* make sure UserImageListFragment is not active after logout */
                navigateTo(mFragmentNavigationItems.get(0));
                getNavigationDrawerLayout().closeDrawer();
                mUserPresenter.refreshUser();
            }

            @Override
            public void onQueryFailed() { }
        }).fetch();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mUserPresenter.refreshUser();
            }
            getNavigationDrawerLayout().deselectMenuItem(R.id.navigationLogin);
            getNavigationDrawerLayout().selectMenuItem(getSelectedMenuItemId());
        }
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
