package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import derpibooru.derpy.R;
import derpibooru.derpy.UserManager;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.LogoutRequester;
import derpibooru.derpy.ui.fragments.UserFragment;
import derpibooru.derpy.ui.views.AccentColorIconButton;

abstract class NavigationDrawerUserFragmentActivity extends NavigationDrawerFragmentActivity {
    public static final String EXTRAS_USER = "derpibooru.derpy.DerpibooruUser";
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;

    private NavigationDrawerHeaderViewHolder mHeader;
    private UserManager mUserManager;

    protected void initialize(Bundle savedInstanceState) {
        super.initializeDrawerAndFragmentNavigation();
        mHeader = new NavigationDrawerHeaderViewHolder(mNavigationView.getHeaderView(0), this);
        mHeader.buttonRefreshUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUser();
            }
        });
        initializeUser(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRAS_USER, getUser());
    }

    @Override
    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment f = super.getFragmentInstance(fragmentMenuItem);
        if (f instanceof UserFragment) {
            f.getArguments().putParcelable(EXTRAS_USER, getUser());
        }
        return f;
    }

    protected DerpibooruUser getUser() throws IllegalStateException {
        return mUserManager.getUser();
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
            throw new IllegalStateException("NavigationDrawerUserFragmentActivity didn't receive DerpibooruUser with the savedInstanceState bundle");
        }
        mUserManager = new UserManager(this, user);
        mUserManager.setOnUserRefreshListener(new UserRefreshListener());
        displayUser();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshUser();
            }
            getNavigationDrawerLayout().deselectMenuItem(R.id.navigationLogin);
            getNavigationDrawerLayout().selectMenuItem(getSelectedMenuItemId());
        }
    }

    private void logout() {
        new LogoutRequester(this, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                setLoggedOutHeader();
                getNavigationDrawerLayout().closeDrawer();
                refreshUser();
            }

            @Override
            public void onQueryFailed() { }
        }).fetch();
    }

    public void refreshUser() {
        mHeader.buttonRefreshUser.setVisibility(View.INVISIBLE);
        mHeader.textCurrentFilter.setText(R.string.loading);
        mUserManager.refresh();
    }

    /**
     * Displays username and current filter, swaps menu items depending on whether the user
     * is logged in or not.
     */
    public void displayUser() {
        mHeader.buttonRefreshUser.setVisibility(View.VISIBLE);
        if (!getUser().isLoggedIn()) {
            setLoggedOutHeader();
        } else {
            setLoggedInHeader(getUser().getUsername());
        }
        mHeader.textCurrentFilter.setText(String.format(getString(R.string.user_filter),
                                                 getUser().getCurrentFilter().getName()));
        /* ! copied from ImageCommentsAdapter; perhaps it should be made into a separate class? */
        if (!getUser().getAvatarUrl().endsWith(".svg")) {
            Glide.with(this)
                    .load(getUser().getAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate()
                    .into(mHeader.viewAvatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_avatar)
                    .dontAnimate()
                    .into(mHeader.viewAvatar);
        }
        setActiveMenuItem();
    }

    private void setLoggedInHeader(String username) {
        mHeader.textUsername.setText(username);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_in);
    }

    private void setLoggedOutHeader() {
        mHeader.textUsername.setText(R.string.user_logged_out);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_out);
    }

    private static class NavigationDrawerHeaderViewHolder {
        AccentColorIconButton buttonRefreshUser;
        TextView textUsername;
        TextView textCurrentFilter;
        ImageView viewAvatar;

        private NavigationDrawerHeaderViewHolder(View v, Context c) {
            buttonRefreshUser = (AccentColorIconButton) v.findViewById(R.id.buttonRefreshUser);
            textUsername = (TextView) v.findViewById(R.id.textHeaderUser);
            textCurrentFilter = (TextView) v.findViewById(R.id.textHeaderFilter);
            viewAvatar = (ImageView) v.findViewById(R.id.imageAvatar);
            /* as per Material Design Guidelines http://www.google.com/design/spec/patterns/navigation-drawer.html */
            textUsername.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Medium.ttf"));
            textCurrentFilter.setTypeface(Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Regular.ttf"));
        }
    }

    private class UserRefreshListener implements UserManager.OnUserRefreshListener {
        @Override
        public void onUserRefreshed(DerpibooruUser user) {
            displayUser();
            if (getCurrentFragment() instanceof UserFragment) {
                ((UserFragment) getCurrentFragment()).setRefreshedUserData(user);
            }
        }

        @Override
        public void onRefreshFailed() {
            /* TODO: show an error message */
        }
    }
}
