package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.server.DerpibooruRankingsListType;
import derpibooru.derpy.server.User;
import derpibooru.derpy.ui.fragments.RankingsTabFragment;
import derpibooru.derpy.ui.fragments.WatchedTabFragment;

public class MainActivityTabAdapter extends FragmentStatePagerAdapter {
    private static final String TITLE_WATCHED = "Watched";
    private static final String TITLE_TOP_SCORING = "Top Scoring";
    private static final String TITLE_MOST_COMMENTED = "Most Commented";

    private ArrayList<String> mTabs = new ArrayList<>();
    private TabSetChangeHandler mTabChangeHandler;
    private Context mContext;

    public MainActivityTabAdapter(Context context, FragmentManager fm,
                                  TabSetChangeHandler tabChangeHandler) {
        super(fm);
        mContext = context;
        mTabChangeHandler = tabChangeHandler;
        mTabs.add(TITLE_TOP_SCORING);
        mTabs.add(TITLE_MOST_COMMENTED);
    }

    public void toggleWatchedTab() {
        boolean loggedIn = new User(mContext).isLoggedIn();
        boolean watchedTabDisplayed = mTabs.contains(TITLE_WATCHED);
        if (loggedIn && !watchedTabDisplayed) {
            mTabs.add(0, TITLE_WATCHED);
            notifyDataSetChanged();
            mTabChangeHandler.onTabSetChanged();
        } else if (!loggedIn && watchedTabDisplayed) {
            mTabs.remove(TITLE_WATCHED);
            notifyDataSetChanged();
            mTabChangeHandler.onTabSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (mTabs.get(position)) {
            case TITLE_TOP_SCORING:
                Bundle args = new Bundle();
                args.putInt("type", DerpibooruRankingsListType.TopScoring.convertToValue());
                RankingsTabFragment fragmentTopScoring = new RankingsTabFragment();
                fragmentTopScoring.setArguments(args);
                return fragmentTopScoring;
            case TITLE_MOST_COMMENTED:
                RankingsTabFragment fragmentMostCommented = new RankingsTabFragment();
                args = new Bundle();
                args.putInt("type", DerpibooruRankingsListType.MostCommented.convertToValue());
                fragmentMostCommented.setArguments(args);
                return fragmentMostCommented;
            case TITLE_WATCHED:
                return new WatchedTabFragment();
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