package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.providers.RankingImageListProvider;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.fragments.UserFragment;
import derpibooru.derpy.ui.fragments.tabs.MainActivityNewImagesTabFragment;
import derpibooru.derpy.ui.fragments.tabs.MainActivityRankingsTabFragment;
import derpibooru.derpy.ui.fragments.tabs.MainActivityWatchedTabFragment;

public class HomeTabAdapter extends FragmentStatePagerAdapter {
    private static final String TITLE_NEW = "New";
    private static final String TITLE_WATCHED = "Watched";
    private static final String TITLE_TOP_SCORING = "Top Scoring";
    private static final String TITLE_MOST_COMMENTED = "Most Commented";

    private ArrayList<String> mTabs = new ArrayList<>(3);
    private TabSetChangeHandler mTabChangeHandler;

    private DerpibooruUser mUser;

    public HomeTabAdapter(FragmentManager fm, TabSetChangeHandler tabChangeHandler, DerpibooruUser user) {
        super(fm);
        mTabChangeHandler = tabChangeHandler;
        mTabs.add(TITLE_NEW);
        if (user.isLoggedIn()) {
            mTabs.add(TITLE_WATCHED);
        }
        mTabs.add(TITLE_TOP_SCORING);
        mTabs.add(TITLE_MOST_COMMENTED);
        mUser = user;
    }

    public void refreshUser(DerpibooruUser user) {
        if (isTabSetChangeRequired(user.isLoggedIn())) {
            notifyDataSetChanged();
            mTabChangeHandler.onTabSetChanged();
        } else if (!mUser.getCurrentFilter().equals(user.getCurrentFilter())) {
            notifyDataSetChanged();
        }
        mUser = user;
    }

    private boolean isTabSetChangeRequired(boolean isUserLoggedIn) {
        boolean watchedTabDisplayed = mTabs.contains(TITLE_WATCHED);
        if (isUserLoggedIn && !watchedTabDisplayed) {
            mTabs.add(1, TITLE_WATCHED);
            return true;
        }
        if (!isUserLoggedIn && watchedTabDisplayed) {
            mTabs.remove(TITLE_WATCHED);
            return true;
        }
        return false;
    }

    @Override
    public Fragment getItem(int position) throws NullPointerException {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.EXTRAS_USER, mUser);
        Fragment fragment = null;
        switch (mTabs.get(position)) {
            case TITLE_NEW:
                fragment = new MainActivityNewImagesTabFragment();
                break;
            case TITLE_TOP_SCORING:
                fragment = new MainActivityRankingsTabFragment();
                args.putInt("type", RankingImageListProvider.RankingsType.TopScoring.toValue());
                break;
            case TITLE_MOST_COMMENTED:
                fragment = new MainActivityRankingsTabFragment();
                args.putInt("type", RankingImageListProvider.RankingsType.MostCommented.toValue());
                break;
            case TITLE_WATCHED:
                fragment = new MainActivityWatchedTabFragment();
                break;
        }
        fragment.setArguments(args);
        return fragment;
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