package derpibooru.derpy.ui.utils;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.UserManager;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public abstract class NavigationDrawerUserPresenter {
    @Bind(R.id.buttonRefreshUser) AccentColorIconButton mButtonRefreshUser;
    @Bind(R.id.textHeaderUser) TextView mTextUsername;
    @Bind(R.id.textHeaderFilter) TextView mTextCurrentFilter;
    @Bind(R.id.imageAvatar) ImageView mViewAvatar;

    private NavigationView mNavigationView;
    private UserManager mUserManager;
    private Context mContext;

    public NavigationDrawerUserPresenter(Context context, DerpibooruUser user) {
        mUserManager = new UserManager(context, user);
        mUserManager.setOnUserRefreshListener(new UserRefreshListener());
        mContext = context;
    }

    public void initializeWithView(NavigationView menu) {
        mNavigationView = menu;
        ButterKnife.bind(this, menu.getHeaderView(0));
        displayUser();
    }

    public DerpibooruUser getUser() throws IllegalStateException {
        return mUserManager.getUser();
    }

    @OnClick(R.id.buttonRefreshUser)
    public void refreshUser() {
        mButtonRefreshUser.setVisibility(View.INVISIBLE);
        mTextCurrentFilter.setText(R.string.loading);
        mUserManager.refresh();
    }

    /**
     * Invoked when user data has been refreshed.
     * <br>
     * Always <strong>set</strong> the active fragment's menu item <strong>checked</strong> after receiving the call,
     * as the menu items may have been swapped if the user's authentication status had changed.
     * @param user refreshed user data
     */
    protected abstract void onUserRefreshed(DerpibooruUser user);

    /**
     * Displays username and current filter, swaps menu items depending on whether the user
     * is logged in or not.
     */
    public void displayUser() {
        mButtonRefreshUser.setVisibility(View.VISIBLE);
        if (!getUser().isLoggedIn()) {
            onUserLoggedOut();
        } else {
            onUserLoggedIn(getUser().getUsername());
        }
        mTextCurrentFilter.setText(String.format(mContext.getString(R.string.user_filter),
                                                 getUser().getCurrentFilter().getName()));
        /* ! copied from ImageCommentsAdapter; perhaps it should be made into a separate class? */
        if (!getUser().getAvatarUrl().endsWith(".svg")) {
            Glide.with(mContext)
                    .load(getUser().getAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .dontAnimate()
                    .into(mViewAvatar);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_avatar)
                    .dontAnimate()
                    .into(mViewAvatar);
        }
    }

    private void onUserLoggedIn(String username) {
        mTextUsername.setText(username);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_in);
    }

    private void onUserLoggedOut() {
        mTextUsername.setText(R.string.user_logged_out);
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.menu_navigation_drawer_logged_out);
    }

    private class UserRefreshListener implements UserManager.OnUserRefreshListener {
        @Override
        public void onUserRefreshed(DerpibooruUser user) {
            displayUser();
            NavigationDrawerUserPresenter.this.onUserRefreshed(user);
        }

        @Override
        public void onRefreshFailed() {
            /* TODO: show an error message */
        }
    }
}
