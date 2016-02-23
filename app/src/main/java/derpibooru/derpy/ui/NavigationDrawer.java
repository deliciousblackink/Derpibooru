package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.UserDataProvider;
import derpibooru.derpy.server.providers.UserImageListProvider;
import derpibooru.derpy.server.requesters.LogoutRequester;

/* TODO: navigation drawer should operate fragments, not activities */
class NavigationDrawer extends NavigationDrawerLayout {
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;

    private UserDataProvider mUserProvider;

    NavigationDrawer(NavigationDrawerActivity parent, DrawerLayout drawer, Toolbar toolbar, NavigationView menu) {
        super(parent, drawer, toolbar, menu);
        setActivityMenuItemId();

        mUserProvider = new UserDataProvider(parent, new UserQueryHandler());
        mUserProvider.fetch();
    }

    @Override
    protected void fetchUserData() {
        mUserProvider.refreshUserData();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return isParentActivitySelected(item.getItemId())
                || isAnotherActivitySelected(item.getItemId())
                || isUserImageListActivitySelected(item.getItemId())
                || isAuthenticationActionSelected(item.getItemId());
    }

    private boolean isParentActivitySelected(int itemId) {
        if (itemId == mParentNavigationId) {
            closeDrawer();
            return true;
        }
        return false;
    }

    private boolean isAnotherActivitySelected(int itemId) {
        for (Map.Entry<Integer, Class<?>> activityItem : ACTIVITY_NAVIGATION_ACTIONS.entrySet()) {
            if (activityItem.getKey() == itemId) {
                deselectParentMenuItemAndCloseDrawer();
                mParent.startActivity(new Intent(mParent, activityItem.getValue()));
                return true;
            }
        }
        return false;
    }

    private boolean isUserImageListActivitySelected(int itemId) {
        Intent intent = new Intent(mParent, UserImageListActivity.class);
        switch (itemId) {
            case R.id.navigationFaves:
                intent.putExtra("type", UserImageListProvider.UserListType.Faved.toValue());
                break;
            case R.id.navigationUpvoted:
                intent.putExtra("type", UserImageListProvider.UserListType.Upvoted.toValue());
                break;
            case R.id.navigationUploaded:
                intent.putExtra("type", UserImageListProvider.UserListType.Uploaded.toValue());
                break;
            default:
                return false;
        }
        deselectParentMenuItemAndCloseDrawer();
        mParent.startActivity(intent);
        return true;
    }

    private boolean isAuthenticationActionSelected(int itemId) {
        if (itemId == R.id.navigationLogin) {
            mParent.startActivityForResult(
                    new Intent(mParent, LoginActivity.class), LOGIN_ACTIVITY_REQUEST_CODE);
            deselectParentMenuItemAndCloseDrawer();
            return true;
        } else if (itemId == R.id.navigationLogout) {
            logout();
            return true; /* don't close the drawer */
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mUserProvider.refreshUserData();
            }
            deselectMenuItem(R.id.navigationLogin);
            selectMenuItem(mParentNavigationId);
        }
    }

    private void setActivityMenuItemId() {
        for (Map.Entry<Integer, Class<?>> activityItem : ACTIVITY_NAVIGATION_ACTIONS.entrySet()) {
            if (activityItem.getValue().equals(mParent.getClass())) {
                mParentNavigationId = activityItem.getKey();
            }
        }
    }

    private void logout() {
        new LogoutRequester(mParent, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                mUserProvider.refreshUserData();
            }

            @Override
            public void onQueryFailed() { }
        }).fetch();
    }

    private class UserQueryHandler implements QueryHandler<DerpibooruUser> {
        @Override
        public void onQueryExecuted(DerpibooruUser result) {
            mParent.onUserDataRefreshed();
            displayUserData(result);
        }

        @Override
        public void onQueryFailed() { }
    }

    private static final Map<Integer, Class<?>> ACTIVITY_NAVIGATION_ACTIONS =
            ImmutableMap.<Integer, Class<?>>builder()
            .put(R.id.navigationHome, MainActivity.class)
            .put(R.id.navigationSearch, SearchActivity.class)
            .put(R.id.navigationFilters, FiltersActivity.class).build();
}
