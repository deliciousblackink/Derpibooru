package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import derpibooru.derpy.R;
import derpibooru.derpy.User;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.LogoutRequester;
import derpibooru.derpy.ui.fragments.FilterListFragment;
import derpibooru.derpy.ui.fragments.HomeFragment;
import derpibooru.derpy.ui.fragments.SearchFragment;
import derpibooru.derpy.ui.utils.NavigationDrawerUserPresenter;

public class MainActivity extends NavigationDrawerFragmentActivity {
    public static final String BUNDLE_USER = "DerpibooruUser";

    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;
    private static final List<NavigationDrawerItem> FRAGMENT_NAVIGATION_ITEMS =
            ImmutableList.<NavigationDrawerItem>builder()
                    .add(new NavigationDrawerItem(R.id.navigationHome, HomeFragment.class))
                    .add(new NavigationDrawerItem(R.id.navigationSearch, SearchFragment.class))
                    .add(new NavigationDrawerItem(R.id.navigationFilters, FilterListFragment.class)).build();

    private NavigationDrawerUserPresenter mUserPresenter;
    private User mUser;

    @Bind(R.id.fragmentLayout) FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initializeNavigationDrawer();
        initializeUser(savedInstanceState);
        navigateTo(FRAGMENT_NAVIGATION_ITEMS.get(0));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelable(BUNDLE_USER, mUser.getUser());
        } catch (IllegalStateException e) {
            /* the exception is to be expected when a configuration change occurs prior to user data being fetched
             * it doesn't have to be handled here as the data is going to be fetched again anyway,
             * though the user management should be moved into a Service sometime */
        }
    }

    @Override
    protected List<NavigationDrawerItem> getFragmentNavigationItems() {
        return FRAGMENT_NAVIGATION_ITEMS;
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
        initializeUserPresenter();
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelable(BUNDLE_USER) != null)) {
            mUser = new User(this, (DerpibooruUser) savedInstanceState.getParcelable(BUNDLE_USER));
            mUser.setOnUserRefreshListener(new UserRefreshListener());
            mUserPresenter.displayUser(mUser.getUser());
            setActiveMenuItem();
        } else {
            mUser = new User(this);
            mUser.setOnUserRefreshListener(new UserRefreshListener());
            mUser.refresh();
        }
    }

    private void initializeUserPresenter() {
        mUserPresenter = new NavigationDrawerUserPresenter(this) {
            @Override
            protected void onUserRefreshRequested() {
                mUser.refresh();
            }};
        mUserPresenter.initializeWithView(mNavigationView);
    }

    private boolean isAuthenticationActionSelected(int itemId) {
        if (itemId == R.id.navigationLogin) {
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_ACTIVITY_REQUEST_CODE);
            getNavigationDrawerLayout().closeDrawer();
            return true;
        } else if (itemId == R.id.navigationLogout) {
            logout();
            return true; /* don't close the drawer */
        }
        return false;
    }

    private void logout() {
        new LogoutRequester(this, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
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

    private class UserRefreshListener implements User.OnUserRefreshListener {
        @Override
        public void onUserRefreshed(DerpibooruUser user) {
            mUserPresenter.displayUser(user);
            setActiveMenuItem();
        }
    }
}
