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
import android.widget.ImageView;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.DataProviderRequestHandler;
import derpibooru.derpy.server.UserDataProvider;
import derpibooru.derpy.storage.UserDataStorage;

class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener {
    private final int ACTIVITY_LOGIN_REQUEST_CODE = 1;

    private UserDataProvider mUserProvider;
    private UserDataStorage mUserDataStorage;
    private Activity mParent;

    private View mNavigationDrawerHeader;
    private DrawerLayout mDrawerLayout;

    public NavigationDrawer(Activity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        mDrawerLayout = drawer;
        mParent = parent;

        initDrawerToggle(parent, toolbar, drawer, menu);
        initUserProvider(parent);

        mNavigationDrawerHeader = ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView)).getHeaderView(0);

        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshUserData();
                    }
                });

        mUserDataStorage = new UserDataStorage(parent);
        setUserDataFromStorage();
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

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (ACTIVITY_LOGIN_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    mUserProvider.fetch();
                }
                ((NavigationView) mDrawerLayout.findViewById(R.id.navigationView))
                        .getMenu().findItem(R.id.navigationLogIn).setChecked(false);
                break;
        }
    }

    private void setUserDataFromStorage() {
        DerpibooruUser user = mUserDataStorage.getUserData();
        if (user == null) {
            refreshUserData();
        } else {
            displayUserData(user);
        }
    }

    private void refreshUserData() {
        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.INVISIBLE);
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText("Loading...");
        mUserProvider.fetch();
    }

    private void displayUserData(DerpibooruUser user) {
        mNavigationDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.VISIBLE);
        if (!user.isLoggedIn()) {
            ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderUser))
                    .setText("Not logged in");
        } else {
            ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderUser))
                    .setText(user.getUsername());
        }
        ((TextView) mNavigationDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText("Filter: " + user.getCurrentFilter().getName());
    }

    private void initUserProvider(Activity parent) {
        mUserProvider = new UserDataProvider(parent, new DataProviderRequestHandler() {
            @Override
            public void onDataFetched(Object result) {
                displayUserData((DerpibooruUser) result);
                mUserDataStorage.setUserData((DerpibooruUser) result);
            }

            @Override
            public void onDataRequestFailed() {

            }
        });
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
}
