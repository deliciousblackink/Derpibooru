package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarCommentListTabFragment;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarFavoritesTabFragment;
import derpibooru.derpy.ui.fragments.tabs.ImageBottomBarInfoTabFragment;
import derpibooru.derpy.ui.views.imagedetailedview.ImageBottomBarView;
import derpibooru.derpy.ui.views.ImageTagView;

public class ImageBottomBarTabAdapter extends FragmentStatePagerAdapter {
    private static final int TAB_INFO_POSITION = 0;
    private static final int TAB_FAVES_POSITION = 1;
    private static final int TAB_COMMENTS_POSITION = 2;

    private DerpibooruImageDetailed mImage;;
    private ImageTagView.OnTagClickListener mTagClickListener;
    private CommentListAdapter.OnCommentCountChangeListener mCommentCountChangeListener;

    public ImageBottomBarTabAdapter(FragmentManager fm, DerpibooruImageDetailed imageDetailed,
                                    ImageTagView.OnTagClickListener tagClickListener,
                                    CommentListAdapter.OnCommentCountChangeListener commentCountChangeListener) {
        super(fm);
        mImage = imageDetailed;
        mTagClickListener = tagClickListener;
        mCommentCountChangeListener = commentCountChangeListener;
        if (fm.getFragments() != null) {
            for (Fragment fragment : fm.getFragments()) {
                setFragmentCallbackHandler(fragment);
            }
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case TAB_INFO_POSITION:
                fragment = new ImageBottomBarInfoTabFragment();
                fragment.setArguments(getDetailedImageBundle());
                break;
            case TAB_FAVES_POSITION:
                fragment = new ImageBottomBarFavoritesTabFragment();
                fragment.setArguments(getDetailedImageBundle());
                break;
            case TAB_COMMENTS_POSITION:
                fragment = new ImageBottomBarCommentListTabFragment();
                fragment.setArguments(getImageIdBundle());
                break;
            default:
                throw new IndexOutOfBoundsException("ImageBottomBarTabAdapter getItem(int): position is out of bounds");
        }
        setFragmentCallbackHandler(fragment);
        return fragment;
    }

    private Bundle getDetailedImageBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ImageActivity.EXTRAS_IMAGE_DETAILED, mImage);
        return bundle;
    }

    private Bundle getImageIdBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(ImageActivity.EXTRAS_IMAGE_ID, mImage.getThumb().getId());
        return bundle;
    }

    private void setFragmentCallbackHandler(Fragment fragment) {
        if (fragment instanceof ImageBottomBarInfoTabFragment) {
            ((ImageBottomBarInfoTabFragment) fragment)
                    .setOnTagClickListener(mTagClickListener);
        } else if (fragment instanceof ImageBottomBarCommentListTabFragment) {
            ((ImageBottomBarCommentListTabFragment) fragment)
                    .setCommentCountChangeListener(mCommentCountChangeListener);
        }
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