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
import derpibooru.derpy.ui.fragments.ImageBottomBarTabFragment;

public class ImageBottomBarTabAdapter extends FragmentPagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm,
                                    DerpibooruImageInfo info,
                                    ImageBottomBarTabHandler heightHandler) {
        super(fm);

        Bundle imageInfoBundle = new Bundle();
        imageInfoBundle.putParcelable("image_info", info);

        mTabs = new ArrayList<>();
        ImageBottomBarInfoTabFragment infoTab = new ImageBottomBarInfoTabFragment();
        infoTab.setArguments(imageInfoBundle);
        infoTab.setContentHeightHandler(heightHandler);
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.ImageInfo.id(), infoTab));

        ImageBottomBarFavoritesTabFragment favesTab = new ImageBottomBarFavoritesTabFragment();
        favesTab.setArguments(imageInfoBundle);
        favesTab.setContentHeightHandler(heightHandler);
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Faves.id(), favesTab));

        ImageBottomBarCommentsTabFragment commentsTab = new ImageBottomBarCommentsTabFragment();
        commentsTab.setArguments(imageInfoBundle);
        commentsTab.setContentHeightHandler(heightHandler);
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Comments.id(), commentsTab));
    }

    public void provideCurrentContentHeight(ImageBottomBarTab tab) {
        ((ImageBottomBarTabFragment) mTabs.get(tab.id()).getContent())
                .provideCurrentContentHeight(tab);
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

    public interface ImageBottomBarTabHandler {
        void onTabHeightProvided(ImageBottomBarTab tab, int newHeight);
    }
}