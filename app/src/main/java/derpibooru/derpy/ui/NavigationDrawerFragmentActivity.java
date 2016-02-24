package derpibooru.derpy.ui;

import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.NavigationDrawerItem;

abstract class NavigationDrawerFragmentActivity extends AppCompatActivity {
    private NavigationDrawerLayout mNavigationDrawer;

    private int mSelectedMenuItemId;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.navigationView) NavigationView mNavigationView;

    /**
     * Provides a List of NavigationDrawerItem used for fragment navigation.
     * @return a List used for fragment navigation
     */
    protected abstract List<NavigationDrawerItem> getFragmentNavigationItems();

    /**
     * Provides the view that holds visible fragments.
     * @return the root view
     */
    protected abstract FrameLayout getContentLayout();

    protected abstract Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem) throws Exception;

    protected NavigationDrawerLayout getNavigationDrawerLayout() {
        return mNavigationDrawer;
    }

    protected int getSelectedMenuItemId() {
        return mSelectedMenuItemId;
    }

    protected void setActiveMenuItem() {
        mNavigationDrawer.selectMenuItem(mSelectedMenuItemId);
    }

    protected void initializeNavigationDrawer() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mNavigationDrawer = new NavigationDrawerLayout(
                this, ((DrawerLayout) findViewById(R.id.drawerLayout)), mToolbar, mNavigationView) {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return NavigationDrawerFragmentActivity.this.onNavigationItemSelected(item);
            }
        };
    }

    protected boolean onNavigationItemSelected(MenuItem item) {
        boolean result =  isCurrentFragmentItemSelected(item.getItemId())
                || isAnotherFragmentItemSelected(item.getItemId());
        return result;
    }

    protected void navigateTo(NavigationDrawerItem item) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getContentLayout().getId(), getFragmentInstance(item)).commit();
            mSelectedMenuItemId = item.getNavigationViewItemId();
            mNavigationDrawer.selectMenuItem(mSelectedMenuItemId);
        } catch (Exception t) {
            Log.e("DrawerFragmentActivity", "failed to initialize a Fragment class", t);
        }
    }

    private boolean isCurrentFragmentItemSelected(int menuId) {
        if (menuId == mSelectedMenuItemId) {
            mNavigationDrawer.closeDrawer();
            return true;
        }
        return false;
    }

    private boolean isAnotherFragmentItemSelected(int menuId) {
        for (NavigationDrawerItem item : getFragmentNavigationItems()) {
            if (item.getNavigationViewItemId() == menuId) {
                mNavigationDrawer.deselectMenuItem(mSelectedMenuItemId);
                mNavigationDrawer.closeDrawer();
                navigateTo(item);
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
