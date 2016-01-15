package derpibooru.derpy.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

import derpibooru.derpy.ui.fragments.ImageCommentsTabFragment;
import derpibooru.derpy.ui.fragments.ImageFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.ImageInfoTabFragment;

public class ImageBottomBarTabAdapter extends FragmentPagerAdapter {
    private ArrayList<Tab> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm) {
        super(fm);

        mTabs = new ArrayList<>();
        mTabs.add(new Tab(ImageBottomBarTabs.ImageInfo.getID(), new ImageInfoTabFragment()));
        mTabs.add(new Tab(ImageBottomBarTabs.Faves.getID(), new ImageFavoritesTabFragment()));
        mTabs.add(new Tab(ImageBottomBarTabs.Comments.getID(), new ImageCommentsTabFragment()));
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

    public enum ImageBottomBarTabs {
        ImageInfo(0),
        Faves(1),
        Comments(2);

        private int mID;

        ImageBottomBarTabs(int id) {
            mID = id;
        }

        public int getID() {
            return mID;
        }
    }
}