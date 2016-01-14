package derpibooru.derpy.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.ui.fragments.MostCommentedFragment;
import derpibooru.derpy.ui.fragments.TopScoringFragment;

public class MainActivityTabAdapter extends FragmentPagerAdapter {
    private ArrayList<Tab> mTabs;

    public MainActivityTabAdapter(FragmentManager fm) {
        super(fm);

        mTabs = new ArrayList<>();
        mTabs.add(new Tab(0, "Top Scoring", new TopScoringFragment()));
        mTabs.add(new Tab(1, "Most Commented", new MostCommentedFragment()));
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

    public class Tab {
        private int mPosition;
        private String mTitle;
        private Fragment mContent;

        public Tab(int position, String title, Fragment content) {
            this.mPosition = position;
            this.mTitle = title;
            this.mContent = content;
        }

        public int getPosition() {
            return mPosition;
        }

        public String getTitle() {
            return mTitle;
        }

        public Fragment getContent() {
            return mContent;
        }
    }
}