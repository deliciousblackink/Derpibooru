package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.server.providers.RankingImageListProvider;
import derpibooru.derpy.server.providers.UserDataProvider;
import derpibooru.derpy.ui.fragments.tabs.MainActivityNewImagesTabFragment;
import derpibooru.derpy.ui.fragments.tabs.MainActivityRankingsTabFragment;
import derpibooru.derpy.ui.fragments.tabs.MainActivityWatchedTabFragment;

public class HomeTabAdapter extends FragmentStatePagerAdapter {
    private static final String TITLE_NEW = "New";
    private static final String TITLE_WATCHED = "Watched";
    private static final String TITLE_TOP_SCORING = "Top Scoring";
    private static final String TITLE_MOST_COMMENTED = "Most Commented";

    private ArrayList<String> mTabs = new ArrayList<>();
    private TabSetChangeHandler mTabChangeHandler;
    private Context mContext;

    public HomeTabAdapter(Context context, FragmentManager fm,
                          TabSetChangeHandler tabChangeHandler) {
        super(fm);
        mContext = context;
        mTabChangeHandler = tabChangeHandler;
        mTabs.add(TITLE_NEW);
        mTabs.add(TITLE_TOP_SCORING);
        mTabs.add(TITLE_MOST_COMMENTED);
    }

    public void toggleWatchedTab(boolean isUserLoggedIn) {
        boolean watchedTabDisplayed = mTabs.contains(TITLE_WATCHED);
        if (isUserLoggedIn && !watchedTabDisplayed) {
            mTabs.add(1, TITLE_WATCHED);
            notifyDataSetChanged();
            mTabChangeHandler.onTabSetChanged();
        } else if (!isUserLoggedIn && watchedTabDisplayed) {
            mTabs.remove(TITLE_WATCHED);
            notifyDataSetChanged();
            mTabChangeHandler.onTabSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (mTabs.get(position)) {
            case TITLE_NEW:
                return new MainActivityNewImagesTabFragment();
            case TITLE_TOP_SCORING:
                Bundle args = new Bundle();
                args.putInt("type", RankingImageListProvider.RankingsType.TopScoring.toValue());
                MainActivityRankingsTabFragment fragmentTopScoring = new MainActivityRankingsTabFragment();
                fragmentTopScoring.setArguments(args);
                return fragmentTopScoring;
            case TITLE_MOST_COMMENTED:
                MainActivityRankingsTabFragment fragmentMostCommented = new MainActivityRankingsTabFragment();
                args = new Bundle();
                args.putInt("type", RankingImageListProvider.RankingsType.MostCommented.toValue());
                fragmentMostCommented.setArguments(args);
                return fragmentMostCommented;
            case TITLE_WATCHED:
                return new MainActivityWatchedTabFragment();
        }
        return null;
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position);
    }

    public interface TabSetChangeHandler {
        void onTabSetChanged();
    }
}