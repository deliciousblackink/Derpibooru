package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.ui.fragments.SearchOptionsTabFragment;
import derpibooru.derpy.ui.fragments.SearchResultTabFragment;

public class SearchResultActivityTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public SearchResultActivityTabAdapter(FragmentManager fm, ViewPager pager, String query) {
        super(fm);

        SearchResultTabFragment fragmentSearchResults = new SearchResultTabFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragmentSearchResults.setArguments(args);

        mTabs = new ArrayList<>();
        mTabs.add(new FragmentAdapterItem(0, "Search Results", fragmentSearchResults));
        mTabs.add(new FragmentAdapterItem(1, "Options", new SearchOptionsTabFragment()));

        /* TODO: an adapter should not hold a reference to the viewpager */
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    DerpibooruSearchOptions newOptions =
                            ((SearchOptionsTabFragment) mTabs.get(1).getContent()).getSelectedOptions();
                    ((SearchResultTabFragment) mTabs.get(0).getContent()).setSearchOptions(newOptions);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
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
