package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.User;

class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener {
    private final int ACTIVITY_LOGIN_REQUEST_CODE = 1;
    private Activity mParent;

    private View mNavigationDrawerHeader;
    private DrawerLayout mDrawerLayout;

    private User mUser;

    public NavigationDrawer(Activity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        mDrawerLayout = drawer;
        mParent = parent;
        mNavigationDrawerHeader = ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView)).getHeaderView(0);

        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshUserData();
                    }
                });
        initDrawerToggle(parent, toolbar, drawer, menu);

        mUser = new User(parent, new UserDataHandler());
        mUser.fetchUserData();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigationLogIn) {
            mParent.startActivityForResult(new Intent(mParent, LoginActivity.class),
                                           ACTIVITY_LOGIN_REQUEST_CODE);
        }
        if (id == R.id.navigationLogOut) {
            mUser.logout();
            /* do not hide the drawer */
            return true;
        }

        closeDrawer();
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (ACTIVITY_LOGIN_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    mUser.refreshUserData();
                }
                ((NavigationView) mDrawerLayout.findViewById(R.id.navigationView))
                        .getMenu().findItem(R.id.navigationLogIn).setChecked(false);
                break;
        }
    }

    private void refreshUserData() {
        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.INVISIBLE);
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText("Loading...");
        mUser.refreshUserData();
    }

    private void displayUserData(DerpibooruUser user) {
        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.VISIBLE);
        if (!user.isLoggedIn()) {
            onUserLoggedOut(user);
        } else {
            onUserLoggedIn(user);
        }
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText("Filter: " + user.getCurrentFilter().getName());
    }

    private void onUserLoggedIn(DerpibooruUser user) {
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderUser))
                .setText(user.getUsername());
        ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView)).getMenu().clear();
        ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView))
                .inflateMenu(R.menu.menu_navigation_drawer_logged_in);
    }

    private void onUserLoggedOut(DerpibooruUser user) {
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderUser))
                .setText("Not logged in");
        ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView)).getMenu().clear();
        ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView))
                .inflateMenu(R.menu.menu_navigation_drawer_logged_out);
    }

    private void initDrawerToggle(Activity parent, Toolbar toolbar,
                                  DrawerLayout drawer, NavigationView menu) {
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(parent, mDrawerLayout, toolbar,
                                          R.string.open_drawer, R.string.close_drawer);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        menu.setNavigationItemSelectedListener(this);
    }

    private class UserDataHandler implements User.UserActionPerformedHandler {
        @Override
        public void onUserDataObtained(DerpibooruUser userData) {
            displayUserData(userData);
        }

        @Override
        public void onFailedLogin() { }

        @Override
        public void onFailedLogout() { }

        @Override
        public void onNetworkError() { }
    }
}
