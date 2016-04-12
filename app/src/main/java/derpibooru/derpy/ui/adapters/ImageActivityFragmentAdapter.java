package derpibooru.derpy.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.fragments.ImageActivityMainFragment;
import derpibooru.derpy.ui.fragments.ImageActivityTagFragment;

public abstract class ImageActivityFragmentAdapter extends FragmentAdapter {
    public ImageActivityFragmentAdapter(FragmentManager fragmentManager, int fragmentLayoutId) {
        super(fragmentManager, fragmentLayoutId);
        boolean isMainFragmentInstantiated = false;
        if (fragmentManager.getFragments() != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                setFragmentCallbackHandlers(fragment);
                if (fragment instanceof ImageActivityMainFragment) {
                    isMainFragmentInstantiated = true;
                }
            }
        }
        if (!isMainFragmentInstantiated) {
            instantiateMainFragment();
            fragmentManager.executePendingTransactions();
        }
    }

    protected abstract boolean isUserLoggedIn();

    public void displayMainFragmentWithToolbar(int imageId) {
        if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            ((ImageActivityMainFragment) getCurrentFragment()).setImageId(imageId);
        }
    }

    public void onImageDetailedFetched() {
        if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            ((ImageActivityMainFragment) getCurrentFragment()).onDetailedImageFetched();
        }
    }

    public void displayTagFragment(int tagId) {
        Bundle args = new Bundle();
        args.putInt(ImageActivityTagFragment.EXTRAS_TAG_ID, tagId);

        ImageActivityTagFragment tagFragment = new ImageActivityTagFragment();
        tagFragment.setArguments(args);

        setFragmentCallbackHandlers(tagFragment);
        display(tagFragment, null,
                R.anim.image_activity_tag_enter,
                R.anim.image_activity_tag_exit,
                R.anim.image_activity_tag_back_stack_pop_enter,
                R.anim.image_activity_tag_back_stack_pop_exit);
    }

    private void instantiateMainFragment() {
        ImageActivityMainFragment mainFragment = new ImageActivityMainFragment();
        mainFragment.setArguments(getMainFragmentArguments());
        setFragmentCallbackHandlers(mainFragment);
        display(mainFragment);
    }

    private Bundle getMainFragmentArguments() {
        Bundle args = new Bundle();
        args.putBoolean(ImageActivityMainFragment.EXTRAS_IS_USER_LOGGED_IN, isUserLoggedIn());
        return args;
    }
}
