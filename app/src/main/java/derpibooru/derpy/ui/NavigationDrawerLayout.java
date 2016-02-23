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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.collect.ImmutableMap;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;

abstract class NavigationDrawerLayout implements NavigationView.OnNavigationItemSelectedListener {
    protected NavigationDrawerActivity mParent;
    protected int mParentNavigationId;

    protected NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private View mDrawerHeader;

    NavigationDrawerLayout(NavigationDrawerActivity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        mParent = parent;

        mDrawerLayout = drawer;
        mNavigationView = ((NavigationView) mDrawerLayout.findViewById(R.id.navigationView));
        mDrawerHeader = mNavigationView.getHeaderView(0);

        mDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshUserData();
                    }
                });

        initDrawerToggle(parent, toolbar, drawer, menu);
    }

    protected abstract void fetchUserData();

    public void refreshUserData() {
        mDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.INVISIBLE);
        ((TextView) mDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText(R.string.loading);
        fetchUserData();
    }

    protected void displayUserData(DerpibooruUser user) {
        mDrawerHeader.findViewById(R.id.buttonRefreshUserData)
                .setVisibility(View.VISIBLE);
        if (!user.isLoggedIn()) {
            onUserLoggedOut(user);
        } else {
            onUserLoggedIn(user);
        }
        ((TextView) mDrawerHeader.findViewById(R.id.textHeaderFilter))
                .setText(String.format(mParent.getString(R.string.user_filter),
                                       user.getCurrentFilter().getName()));
        /* ! copied from ImageCommentsAdapter; perhaps it should be made into a separate class? */
        if (!user.getAvatarUrl().endsWith(".svg")) {
            Glide.with(mParent).load(user.getAvatarUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate().into((ImageView) mDrawerHeader.findViewById(R.id.imageAvatar));
        } else {
            Glide.with(mParent).load(R.drawable.no_avatar).dontAnimate()
                    .into((ImageView) mDrawerHeader.findViewById(R.id.imageAvatar));
        }
    }

    private void onUserLoggedIn(DerpibooruUser user) {
        ((TextView) mDrawerHeader.findViewById(R.id.textHeaderUser))
                .setText(user.getUsername());
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_in);
        selectMenuItem(mParentNavigationId);
    }

    private void onUserLoggedOut(DerpibooruUser user) {
        ((TextView) mDrawerHeader.findViewById(R.id.textHeaderUser))
                .setText(R.string.user_logged_out);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_out);
        selectMenuItem(mParentNavigationId);
    }

    private void initDrawerToggle(Activity parent, Toolbar toolbar,
                                  DrawerLayout drawer, NavigationView menu) {
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

    protected void deselectParentMenuItemAndCloseDrawer() {
        deselectMenuItem(mParentNavigationId);
        closeDrawer();
    }

    protected void selectMenuItem(int itemId) {
        mNavigationView.getMenu().findItem(mParentNavigationId).setChecked(true);
    }

    protected void deselectMenuItem(int itemId) {
        mNavigationView.getMenu().findItem(mParentNavigationId).setChecked(false);
    }
}
