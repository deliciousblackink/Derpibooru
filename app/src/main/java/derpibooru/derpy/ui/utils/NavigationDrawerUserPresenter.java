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
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public abstract class NavigationDrawerUserPresenter {
    @Bind(R.id.buttonRefreshUser) AccentColorIconButton mButtonRefreshUser;
    @Bind(R.id.textHeaderUser) TextView mTextUsername;
    @Bind(R.id.textHeaderFilter) TextView mTextCurrentFilter;
    @Bind(R.id.imageAvatar) ImageView mViewAvatar;

    private NavigationView mNavigationView;
    private Context mContext;

    public NavigationDrawerUserPresenter(Context context, NavigationView menu) {
        ButterKnife.bind(context, menu.getHeaderView(0));
        mNavigationView = menu;
    }
    /**
     * Called internally (from the presenter class) when user refresh is requested,
     * either from the UI or by calling the refreshUser() method.
     * Don't forget to call displayUser(DerpibooruUser) with updated data.
     */
    protected abstract void onUserRefreshRequested();


    @OnClick(R.id.buttonRefreshUser)
    public void refreshUser() {
        mButtonRefreshUser.setVisibility(View.INVISIBLE);
        mTextCurrentFilter.setText(R.string.loading);
        onUserRefreshRequested();
    }

    public void displayUser(DerpibooruUser user) {
        mButtonRefreshUser.setVisibility(View.VISIBLE);
        if (!user.isLoggedIn()) {
            onUserLoggedOut();
        } else {
            onUserLoggedIn(user.getUsername());
        }
        mTextCurrentFilter.setText(String.format(mContext.getString(R.string.user_filter),
                                                 user.getCurrentFilter().getName()));
        /* ! copied from ImageCommentsAdapter; perhaps it should be made into a separate class? */
        if (!user.getAvatarUrl().endsWith(".svg")) {
            Glide.with(mContext)
                    .load(user.getAvatarUrl())
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
}
