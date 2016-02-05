package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.fragments.ImageBottomBarCommentsTabFragment;
import derpibooru.derpy.ui.fragments.ImageBottomBarFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.ImageBottomBarInfoTabFragment;

public class ImageBottomBarTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm,
                                    DerpibooruImageInfo info,
                                    ViewPagerContentHeightChangeHandler heightHandler) {
        super(fm);

        Bundle imageInfoBundle = new Bundle();
        imageInfoBundle.putParcelable("image_info", info);

        ImageBottomBarInfoTabFragment infoTab = new ImageBottomBarInfoTabFragment();
        infoTab.setArguments(imageInfoBundle);
        ImageBottomBarFavoritesTabFragment favesTab = new ImageBottomBarFavoritesTabFragment();
        favesTab.setArguments(imageInfoBundle);
        ImageBottomBarCommentsTabFragment commentsTab = new ImageBottomBarCommentsTabFragment();
        commentsTab.setArguments(imageInfoBundle);

        mTabs = new ArrayList<>();
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTabs.ImageInfo.id(), infoTab));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTabs.Faves.id(), favesTab));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTabs.Comments.id(), commentsTab));

        infoTab.setContentHeightHandler(heightHandler);
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

    public interface ViewPagerContentHeightChangeHandler {
        void childHeightUpdated(int newHeight);
    }
}