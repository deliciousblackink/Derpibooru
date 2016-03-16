package derpibooru.derpy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import derpibooru.derpy.R;
import derpibooru.derpy.data.internal.NavigationDrawerItem;
import derpibooru.derpy.data.internal.NavigationDrawerLinkItem;
import derpibooru.derpy.server.providers.RankingImageListProvider;
import derpibooru.derpy.ui.fragments.BrowseFragment;
import derpibooru.derpy.ui.fragments.BrowseImageListFragment;
import derpibooru.derpy.ui.fragments.FilterListFragment;
import derpibooru.derpy.ui.fragments.ImageListFragment;
import derpibooru.derpy.ui.fragments.RankingImageListFragment;

public class MainActivity extends NavigationDrawerUserFragmentActivity {
    private List<NavigationDrawerItem> mFragmentNavigationItems;

    @Bind(R.id.fragmentLayout) FrameLayout mFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFragmentNavigationItems();
        super.initialize(savedInstanceState);
        if (getSupportFragmentManager().getFragments() == null) {
            navigateTo(getFragmentNavigationItems().get(0));
        }
        setCallbackHandlersFor(super.getCurrentFragment());
    }

    @NonNull
    @Override
    protected List<NavigationDrawerItem> getFragmentNavigationItems() {
        return mFragmentNavigationItems;
    }

    @Override
    protected FrameLayout getContentLayout() {
        return mFragmentLayout;
    }

    private void initializeFragmentNavigationItems() {
        /* FIXME: keeping multiple instances of Bundle to store ints is inefficient resource-wise */
        Bundle topScoring = new Bundle();
        topScoring.putInt(RankingImageListFragment.EXTRAS_RANKING_LIST_TYPE, RankingImageListProvider.RankingsType.TopScoring.toValue());
        Bundle mostCommented = new Bundle();
        mostCommented.putInt(RankingImageListFragment.EXTRAS_RANKING_LIST_TYPE, RankingImageListProvider.RankingsType.MostCommented.toValue());
        Bundle watched = new Bundle();
        watched.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserWatched.toValue());
        Bundle faved = new Bundle();
        faved.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserFaved.toValue());
        Bundle upvoted = new Bundle();
        upvoted.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserUpvoted.toValue());
        Bundle uploaded = new Bundle();
        uploaded.putInt(BrowseFragment.EXTRAS_IMAGE_LIST_TYPE, BrowseImageListFragment.Type.UserUploaded.toValue());
        mFragmentNavigationItems = Arrays.asList(
                new NavigationDrawerItem(R.id.navigationBrowse, BrowseFragment.class),
                new NavigationDrawerItem(R.id.navigationFilters, FilterListFragment.class),
                new NavigationDrawerItem(R.id.navigationTopScoring, RankingImageListFragment.class, topScoring),
                new NavigationDrawerItem(R.id.navigationMostCommented, RankingImageListFragment.class, mostCommented),
                new NavigationDrawerLinkItem(R.id.naviationWatched,
                                             new NavigationDrawerItem(R.id.navigationBrowse, BrowseFragment.class, watched)),
                new NavigationDrawerLinkItem(R.id.navigationFaves,
                                             new NavigationDrawerItem(R.id.navigationBrowse, BrowseFragment.class, faved)),
                new NavigationDrawerLinkItem(R.id.navigationUpvoted,
                                             new NavigationDrawerItem(R.id.navigationBrowse, BrowseFragment.class, upvoted)),
                new NavigationDrawerLinkItem(R.id.navigationUploaded,
                                             new NavigationDrawerItem(R.id.navigationBrowse, BrowseFragment.class, uploaded))
        );
    }

    private void setCallbackHandlersFor(Fragment fragment) {
        if (fragment instanceof FilterListFragment) {
            ((FilterListFragment) fragment).setOnFilterChangeListener(new FilterListFragment.OnFilterChangeListener() {
                @Override
                public void onFilterChanged() {
                    MainActivity.super.refreshUser();
                }
            });
        }
    }

    @Override
    protected Fragment getFragmentInstance(NavigationDrawerItem fragmentMenuItem)
            throws IllegalAccessException, InstantiationException {
        Fragment fragment = super.getFragmentInstance(fragmentMenuItem);
        setCallbackHandlersFor(fragment);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        /* https://code.google.com/p/android/issues/detail?id=40323 */
        if (!((getCurrentFragment() instanceof BrowseFragment)
                && ((BrowseFragment) getCurrentFragment()).popChildFragmentManagerBackstack())) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* see the docs for ImageListFragment */
        if ((getCurrentFragment() instanceof BrowseFragment)
                || (getCurrentFragment() instanceof ImageListFragment)) {
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionRandomImage:
                /* TODO: random image */
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
