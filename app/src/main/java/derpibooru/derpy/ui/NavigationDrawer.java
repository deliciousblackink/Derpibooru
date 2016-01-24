package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import derpibooru.derpy.R;

class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener {
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
            mParent.startActivity(new Intent(mParent, LoginActivity.class));
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
