package derpibooru.derpy.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.fragments.ImageBottomBarCommentsTabFragment;
import derpibooru.derpy.ui.fragments.ImageBottomBarFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.ImageBottomBarInfoTabFragment;
import derpibooru.derpy.ui.fragments.ImageBottomBarTabFragment;

public class ImageBottomBarTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm) {
        super(fm);

        mTabs = new ArrayList<>();
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.ImageInfo.id(),
                                          new ImageBottomBarInfoTabFragment()));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Faves.id(),
                                          new ImageBottomBarFavoritesTabFragment()));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Comments.id(),
                                          new ImageBottomBarCommentsTabFragment()));
    }

    public void setTabInfo(DerpibooruImageInfo info) {
        for (FragmentAdapterItem item : mTabs) {
            item.getContent().getArguments().putParcelable("info", info);
            ((ImageBottomBarTabFragment) item.getContent()).onTabInfoFetched(info);
        }
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

    public enum ImageBottomBarTab {
        ImageInfo(0),
        Faves(1),
        Comments(2);

        private int mId;

        ImageBottomBarTab(int id) {
            mId = id;
        }

        public int id() {
            return mId;
        }

        public static ImageBottomBarTab fromId(int id) {
            for (ImageBottomBarTab tab : values()) {
                if (tab.mId == id) {
                    return tab;
                }
            }
            return null;
        }
    }
}