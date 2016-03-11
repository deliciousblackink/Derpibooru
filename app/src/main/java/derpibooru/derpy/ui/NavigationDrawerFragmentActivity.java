package derpibooru.derpy.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.data.internal.NavigationDrawerLinkItem;

abstract class NavigationDrawerFragmentActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String EXTRAS_CURRENT_MENU_ITEM_ID = "derpibooru.derpy.NavDrawerSelectedItemId";
    private NavigationDrawerLayout mNavigationDrawer;

    private int mSelectedMenuItemId;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.navigationView) NavigationView mNavigationView;

    /**
     * Provides a List of NavigationDrawerItem used for fragment navigation.
     *
     * @return a List used for fragment navigation
     */
    @NonNull
    protected abstract List<NavigationDrawerItem> getFragmentNavigationItems();

    /**
     * Provides the view that holds visible fragments.
     *
     * @return the root view
     */
    protected abstract FrameLayout getContentLayout();

    /**
     * Provides an initialized instance of a fragment requested from a menu item. Override
     * this method to provide additional arguments depending on the fragment class:
     * <pre>{@code      @Override
     * protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem) {
     *     Fragment f = super.getFragmentInstance(fragmentMenuItem);
     *     if (f instanceof RequiredFragmentClass) {
     *         f.getArguments().put ...
     *     }
     *     return f;
     * }}</pre>
     *
     * @param fragmentMenuItem fragment menu item
     * @return initialized fragment
     */
    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment f = fragmentMenuItem.getFragmentClass().newInstance();
        f.setArguments(new Bundle());
        if (fragmentMenuItem.getFragmentArguments() != null) {
            f.setArguments(fragmentMenuItem.getFragmentArguments());
        }
        return f;
    }

    @Nullable
    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(getContentLayout().getId());
    }

    protected NavigationDrawerLayout getNavigationDrawerLayout() {
        return mNavigationDrawer;
    }

    protected int getSelectedMenuItemId() {
        return mSelectedMenuItemId;
    }

    protected void setActiveMenuItem() {
        mNavigationDrawer.selectMenuItem(mSelectedMenuItemId);
    }

    protected void initializeDrawerAndFragmentNavigation() {
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
        boolean result = isCurrentFragmentItemSelected(item.getItemId())
                || isAnotherFragmentItemSelected(item.getItemId());
        return result;
    }

    /**
     * Replaces the currently visible fragment with the one specified by a NavigationDrawerItem.
     */
    protected void navigateTo(NavigationDrawerItem item) {
        try {
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            if (getCurrentFragment() != null) {
                transaction.addToBackStack(null);
            }
            transaction
                    .replace(getContentLayout().getId(), getFragmentInstance(item), item.getFragmentClass().getName())
                    .commit();
            selectMenuItem(item);
        } catch (Exception t) {
            Log.e("DrawerFragmentActivity", "failed to initialize a Fragment class", t);
        }
    }

    /**
     * Checks if the menu item specified belongs to a currently displayed fragment. If so,
     * closes the navigation drawer.
     *
     * @param menuId menu item
     * @return <strong>true</strong> if menu item belongs to the current fragment; <strong>false</strong> otherwise.
     */
    private boolean isCurrentFragmentItemSelected(int menuId) {
        if (menuId == mSelectedMenuItemId) {
            mNavigationDrawer.closeDrawer();
            return true;
        }
        return false;
    }

    /**
     * Checks if the menu item specified belongs to a fragment not currently displayed. If so,
     * closes the navigation drawer and navigates to the fragment.
     *
     * @param menuId menu item
     * @return <strong>true</strong> if navigated to another fragment; <strong>false</strong> otherwise.
     */
    private boolean isAnotherFragmentItemSelected(int menuId) {
        for (NavigationDrawerItem item : getFragmentNavigationItems()) {
            if ((item.getNavigationViewItemId() == menuId) ||
                    ((item instanceof NavigationDrawerLinkItem)
                            && (((NavigationDrawerLinkItem) item).getLinkNavigationViewItemId() == menuId))) {
                mNavigationDrawer.deselectMenuItem(mSelectedMenuItemId);
                mNavigationDrawer.closeDrawer();
                navigateTo(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up a NavigationDrawerItem corresponding to the fragment.
     * <br>
     * Note: the tag is equal to Class.getName(). Such tag is applied for fragments
     * displayed via {@link #navigateTo(NavigationDrawerItem)}.
     */
    protected NavigationDrawerItem findNavigationItemByFragmentTag(final String tag) {
        return Iterables.find(
                getFragmentNavigationItems(), new Predicate<NavigationDrawerItem>() {
                    @Override
                    public boolean apply(NavigationDrawerItem item) {
                        return item.getFragmentClass().getName().equals(tag);
                    }
                });
    }

    private void selectMenuItem(NavigationDrawerItem fragmentItem) {
        mSelectedMenuItemId = fragmentItem.getNavigationViewItemId();
        mNavigationDrawer.selectMenuItem(mSelectedMenuItemId);
    }

    /**
     * Restores the menu item selected prior to a configuration change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState != null) {
            mSelectedMenuItemId = savedInstanceState.getInt(EXTRAS_CURRENT_MENU_ITEM_ID);
        }
    }

    /**
     * Preserves the menu item selected prior to a configuration change.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRAS_CURRENT_MENU_ITEM_ID, mSelectedMenuItemId);
    }

    /**
     * Sets the toolbar title and menu position for fragments displayed from a back stack.
     */
    @Override
    public void onBackStackChanged() {
        final Fragment fragment = getCurrentFragment();
        if (fragment != null) {
            selectMenuItem(findNavigationItemByFragmentTag(fragment.getTag()));
        }
    }

    /**
     * Closes the navigation drawer on back button pressed.
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
