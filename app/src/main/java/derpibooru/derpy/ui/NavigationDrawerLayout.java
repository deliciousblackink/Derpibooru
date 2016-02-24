package derpibooru.derpy.ui;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import derpibooru.derpy.R;

abstract class NavigationDrawerLayout implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    NavigationDrawerLayout(NavigationDrawerFragmentActivity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        mDrawerLayout = drawer;
        mNavigationView = menu;
        initDrawerToggle(parent, toolbar, drawer, menu);
    }

    private void initDrawerToggle(Activity parent, Toolbar toolbar, DrawerLayout drawer, NavigationView menu) {
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(parent, mDrawerLayout, toolbar,
                                          R.string.open_drawer, R.string.close_drawer) {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        /* Disable the hamburger icon animation (for more info refer to
                         * https://medium.com/android-news/navigation-drawer-styling-according-material-design-5306190da08f#.9wrzhczd8 ) */
                        super.onDrawerSlide(drawerView, 0);
                    }
                };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        menu.setNavigationItemSelectedListener(this);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    protected void selectMenuItem(int itemId) {
        mNavigationView.getMenu().findItem(itemId).setChecked(true);
    }

    protected void deselectMenuItem(int itemId) {
        mNavigationView.getMenu().findItem(itemId).setChecked(false);
    }
}
