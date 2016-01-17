package derpibooru.derpy.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.types.FragmentTab;
import derpibooru.derpy.ui.fragments.MostCommentedFragment;
import derpibooru.derpy.ui.fragments.TopScoringFragment;

public class MainActivityTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentTab> mTabs;

    public MainActivityTabAdapter(FragmentManager fm) {
        super(fm);

        mTabs = new ArrayList<>();
        mTabs.add(new FragmentTab(0, "Top Scoring", new TopScoringFragment()));
        mTabs.add(new FragmentTab(1, "Most Commented", new MostCommentedFragment()));
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