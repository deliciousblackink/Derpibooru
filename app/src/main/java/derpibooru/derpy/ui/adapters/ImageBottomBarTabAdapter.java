package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.ui.fragments.ImageCommentsTabFragment;
import derpibooru.derpy.ui.fragments.ImageFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.ImageInfoTabFragment;

public class ImageBottomBarTabAdapter extends FragmentPagerAdapter {
    private ArrayList<Tab> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm,
                                    DerpibooruImageInfo info) {
        super(fm);

        Bundle imageInfoBundle = new Bundle();
        imageInfoBundle.putParcelable("image_info", info);

        ImageInfoTabFragment infoTab = new ImageInfoTabFragment();
        infoTab.setArguments(imageInfoBundle);
        ImageFavoritesTabFragment favesTab = new ImageFavoritesTabFragment();
        favesTab.setArguments(imageInfoBundle);
        ImageCommentsTabFragment commentsTab = new ImageCommentsTabFragment();
        commentsTab.setArguments(imageInfoBundle);

        mTabs = new ArrayList<>();
        mTabs.add(new Tab(ImageBottomBarTabs.ImageInfo.id(), infoTab));
        mTabs.add(new Tab(ImageBottomBarTabs.Faves.id(), favesTab));
        mTabs.add(new Tab(ImageBottomBarTabs.Comments.id(), commentsTab));
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

        public int id() {
            return mID;
        }
    }
}