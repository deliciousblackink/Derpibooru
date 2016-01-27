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
import derpibooru.derpy.server.DataProviderRequestHandler;
import derpibooru.derpy.server.UserDataProvider;

class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener {
    private final int ACTIVITY_LOGIN_REQUEST_CODE = 1;

    private UserDataProvider mUserProvider;
    private DrawerLayout mDrawerLayout;
    private Activity mParent;

    public NavigationDrawer(Activity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        mDrawerLayout = drawer;
        mParent = parent;
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(parent, mDrawerLayout, toolbar,
                                          R.string.open_drawer, R.string.close_drawer);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        menu.setNavigationItemSelectedListener(this);

        mUserProvider = new UserDataProvider(parent, new DataProviderRequestHandler() {
            @Override
            public void onDataFetched(Object result) {
                updateUserData((DerpibooruUser) result);
            }

            @Override
            public void onDataRequestFailed() {

            }
        });
        mUserProvider.fetch();
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
                break;
        }
    }

    private void updateUserData(DerpibooruUser user) {
        View header = ((NavigationView)
                mDrawerLayout.findViewById(R.id.navigationView)).getHeaderView(0);
        if (!user.isLoggedIn()) {
            ((TextView) header.findViewById(R.id.textHeaderUser))
                    .setText("Not logged in");
        } else {
            ((TextView) header.findViewById(R.id.textHeaderUser))
                    .setText(user.getUsername());
        }
        ((TextView) header.findViewById(R.id.textHeaderFilter))
                .setText("Filter: " + user.getCurrentFilter().getName());
    }
}
