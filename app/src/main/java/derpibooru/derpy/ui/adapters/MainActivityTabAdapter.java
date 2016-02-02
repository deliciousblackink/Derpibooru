package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruImageListType;
import derpibooru.derpy.ui.fragments.RankingsTabFragment;

public class MainActivityTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public MainActivityTabAdapter(FragmentManager fm) {
        super(fm);

        mTabs = new ArrayList<>();

        /* TODO: do not initialize the tabs right away! */
        RankingsTabFragment fragmentTopScoring = new RankingsTabFragment();
        Bundle args = new Bundle();
        args.putInt("type", DerpibooruImageListType.TopScoring.convertToValue());
        fragmentTopScoring.setArguments(args);

        RankingsTabFragment fragmentMostCommented = new RankingsTabFragment();
        args = new Bundle();
        args.putInt("type", DerpibooruImageListType.MostCommented.convertToValue());
        fragmentMostCommented.setArguments(args);

        mTabs.add(new FragmentAdapterItem(0, "Top Scoring", fragmentTopScoring));
        mTabs.add(new FragmentAdapterItem(1, "Most Commented", fragmentMostCommented));
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