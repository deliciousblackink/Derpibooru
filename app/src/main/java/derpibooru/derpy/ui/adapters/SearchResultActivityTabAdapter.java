package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.ui.fragments.SearchResultTabFragment;
import derpibooru.derpy.ui.fragments.SearchOptionsTabFragment;

public class SearchResultActivityTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public SearchResultActivityTabAdapter(FragmentManager fm, String query) {
        super(fm);

        mTabs = new ArrayList<>();

        SearchResultTabFragment fragmentSearchResults = new SearchResultTabFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragmentSearchResults.setArguments(args);

        mTabs.add(new FragmentAdapterItem(0, "Search Results", fragmentSearchResults));
        mTabs.add(new FragmentAdapterItem(1, "Options", new SearchOptionsTabFragment()));
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
}
