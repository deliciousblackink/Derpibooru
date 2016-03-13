package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import derpibooru.derpy.data.internal.FragmentAdapterItem;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarCommentListTabFragment;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarInfoTabFragment;
import derpibooru.derpy.ui.views.ImageTagView;

public abstract class ImageBottomBarTabAdapter extends FragmentStatePagerAdapter {
    private ArrayList<FragmentAdapterItem> mTabs;

    public ImageBottomBarTabAdapter(FragmentManager fm, DerpibooruImageDetailed imageDetailed) {
        super(fm);
        Bundle imageDetailedArgs = new Bundle();
        imageDetailedArgs.putParcelable(ImageActivity.EXTRAS_IMAGE_DETAILED, imageDetailed);
        Bundle imageIdArgs = new Bundle();
        imageIdArgs.putInt(ImageActivity.EXTRAS_IMAGE_ID, imageDetailed.getThumb().getId());

        ImageBottomBarInfoTabFragment info = new ImageBottomBarInfoTabFragment();
        info.setArguments(imageDetailedArgs);
        ImageBottomBarFavoritesTabFragment faves = new ImageBottomBarFavoritesTabFragment();
        faves.setArguments(imageDetailedArgs);
        ImageBottomBarCommentListTabFragment comments = new ImageBottomBarCommentListTabFragment();
        comments.setArguments(imageIdArgs);

        mTabs = new ArrayList<>(3);
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.ImageInfo.id(), info));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Faves.id(), faves));
        mTabs.add(new FragmentAdapterItem(ImageBottomBarTab.Comments.id(), comments));
    }

    public abstract void onTagClicked(int tagId);

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        /* ensure getItem(int) is called on restore to reset the ImageBottomBarInfoTabFragment listener */
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = mTabs.get(position).getContent();
        if (f instanceof ImageBottomBarInfoTabFragment) {
            ((ImageBottomBarInfoTabFragment) f).setOnTagClickListener(new ImageTagView.OnTagClickListener() {
                @Override
                public void onTagClicked(int tagId) {
                    ImageBottomBarTabAdapter.this.onTagClicked(tagId);
                }
            });
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
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