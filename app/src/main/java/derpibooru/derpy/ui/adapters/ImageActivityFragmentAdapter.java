package derpibooru.derpy.ui.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.fragments.ImageActivityMainFragment;
import derpibooru.derpy.ui.fragments.ImageActivityTagFragment;
import derpibooru.derpy.ui.fragments.ImageListFragment;

public abstract class ImageActivityFragmentAdapter extends FragmentAdapter {
    public ImageActivityFragmentAdapter(FragmentManager fragmentManager,
                                        int fragmentLayoutId,
                                        @Nullable Bundle savedInstanceState,
                                        @Nullable DerpibooruImageThumb placeholderThumb) {
        super(fragmentManager, fragmentLayoutId);
        setFragmentCallbackHandlers(getCurrentFragment());
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(ImageActivity.EXTRAS_IMAGE_DETAILED))) {
            if (getCurrentFragment() instanceof ImageActivityMainFragment) {
                forceMainFragmentToDisplayDetailedImage(getCurrentFragment());
            }
        } else if (placeholderThumb != null) {
            displayMainFragmentWithPlaceholderThumb(placeholderThumb);
        }
    }

    protected abstract boolean isUserLoggedIn();

    public void onDetailedImageFetched() {
        displayMainFragmentWithDetailed();
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

    /**
     * If {@link ImageActivityMainFragment} has not created its view yet, it is forced to
     * skip the placeholder thumb and call {@link ImageActivityMainFragment.ImageActivityMainFragmentHandler#getImage()}.
     */
    private void forceMainFragmentToDisplayDetailedImage(Fragment mainFragment) {
        mainFragment.getArguments().remove(ImageListFragment.EXTRAS_IMAGE_THUMB);
    }

    private void displayMainFragmentWithPlaceholderThumb(DerpibooruImageThumb placeholder) {
        instantiateMainFragment(placeholder);
    }

    private void displayMainFragmentWithDetailed() {
        if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            /* the main fragment has already been instantiated with a placeholder thumb */
            ((ImageActivityMainFragment) getCurrentFragment())
                    .onDetailedImageFetched();
        } else {
            instantiateMainFragment(null);
        }
    }

    private Bundle getMainFragmentArguments(@Nullable DerpibooruImageThumb placeholder) {
        Bundle args = new Bundle();
        args.putBoolean(ImageActivityMainFragment.EXTRAS_IS_USER_LOGGED_IN, isUserLoggedIn());
        if (placeholder != null) {
            args.putParcelable(ImageListFragment.EXTRAS_IMAGE_THUMB, placeholder);
        }
        return args;
    }

    /*public void initializeMainFragment() {
        if (getCurrentFragment() instanceof ImageActivityMainFragment) {
            setFragmentCallbackHandlers(getCurrentFragment());
            forceMainFragmentToDisplayDetailedImage(getCurrentFragment());
        }
    }*/

    private void instantiateMainFragment(DerpibooruImageThumb placeholderThumb) {
        ImageActivityMainFragment mainFragment = new ImageActivityMainFragment();
        mainFragment.setArguments(getMainFragmentArguments(placeholderThumb));

        setFragmentCallbackHandlers(mainFragment);
        display(mainFragment);
    }
}
