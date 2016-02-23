package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.ui.fragments.tabs.SearchOptionsTabFragment;
import derpibooru.derpy.ui.fragments.tabs.SearchResultTabFragment;

public class SearchResultActivityTabAdapter extends FragmentPagerAdapter {
    private static final String SEARCH_RESULTS_TAB_TITLE = "Search results";
    private static final int SEARCH_RESULTS_TAB_POSITION = 0;
    private static final String SEARCH_OPTIONS_TAB_TITLE = "Options";
    private static final int SEARCH_OPTIONS_TAB_POSITION = 1;

    private ArrayList<FragmentAdapterItem> mTabs = new ArrayList<>();
    private FragmentManager mFragmentManager;

    public SearchResultActivityTabAdapter(FragmentManager fm, String query) {
        super(fm);
        mFragmentManager = fm;

        SearchResultTabFragment results = new SearchResultTabFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        results.setArguments(args);
        mTabs.add(new FragmentAdapterItem(SEARCH_RESULTS_TAB_POSITION, SEARCH_RESULTS_TAB_TITLE, results));
        mTabs.add(new FragmentAdapterItem(SEARCH_OPTIONS_TAB_POSITION, SEARCH_OPTIONS_TAB_TITLE,
                                          new SearchOptionsTabFragment()));
    }

    public void transferSearchOptionsToSearchResultsTab(int viewPagerId) {
        String searchOptionsTabTag = getFragmentTag(viewPagerId, SEARCH_OPTIONS_TAB_POSITION);
        String searchResultsTabTag = getFragmentTag(viewPagerId, SEARCH_RESULTS_TAB_POSITION);

        DerpibooruSearchOptions newOptions =
                ((SearchOptionsTabFragment) mFragmentManager.findFragmentByTag(searchOptionsTabTag)).getSelectedOptions();
        ((SearchResultTabFragment) mFragmentManager.findFragmentByTag(searchResultsTabTag)).setSearchOptions(newOptions);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).getContent();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

    private String getFragmentTag(int viewPagerId, int position) {
        /* http://stackoverflow.com/a/11976663/1726690 */
        return "android:switcher:" + viewPagerId + ":" + position;
    }
}
