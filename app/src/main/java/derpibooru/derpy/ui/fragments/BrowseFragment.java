package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.MainActivity;

public class BrowseFragment extends NavigationDrawerUserFragment {
    public static final String EXTRAS_SEARCH_OPTIONS = "derpibooru.derpy.SearchOptions";
    public static final String EXTRAS_IMAGE_LIST_TYPE = "derpibooru.derpy.BrowseImageListType";
    private static final String EXTRAS_OPTIONS_FRAGMENT_SAVED_STATE = "derpibooru.derpy.OptionsNestedState";
    private static final String EXTRAS_IMAGE_LIST_FRAGMENT_SAVED_STATE = "derpibooru.derpy.ImageNestedState";

    private DerpibooruSearchOptions mCurrentSearchOptions = new DerpibooruSearchOptions();
    private Fragment.SavedState mImageListRetainedState;

    private String mTagSearchRequest = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse, container, false);
        ButterKnife.bind(this, v);
        if ((!isDisplayingTagSearchRequest()) && (!isRestoredFromSavedInstance(savedInstanceState))) {
            if (getArguments().containsKey(EXTRAS_IMAGE_LIST_TYPE)) {
                setSearchOptionsFromImageListType(BrowseImageListFragment.Type.fromValue(
                        getArguments().getInt(EXTRAS_IMAGE_LIST_TYPE)));
            }
            displayImageListFragment(null);
        }
        return v;
    }

    private void updateActionBarTitleForImageListFragment() {
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            if (!mCurrentSearchOptions.getSearchQuery().equals("*")) {
                bar.setTitle(mCurrentSearchOptions.getSearchQuery());
            } else {
                bar.setTitle(getString(
                        mCurrentSearchOptions.isDisplayingWatchedTagsOnly() ? R.string.image_list_watched :
                        mCurrentSearchOptions.isDisplayingFavesOnly() ? R.string.image_list_faved :
                        mCurrentSearchOptions.isDisplayingUpvotesOnly() ? R.string.image_list_upvotes :
                        mCurrentSearchOptions.isDisplayingUploadsOnly() ? R.string.image_list_uploads :
                        R.string.image_list));
            }
        }
    }

    private void updateActionBarTitleForOptionsFragment() {
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.image_list_options));
        }
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser newUser) {
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            ((NavigationDrawerUserFragment) getCurrentFragment()).setRefreshedUserData(newUser);
        } else {
            /* make the image list refresh when the user returns to it by setting its retained state to null */
            mImageListRetainedState = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRAS_SEARCH_OPTIONS, mCurrentSearchOptions);
        /* https://code.google.com/p/android/issues/detail?id=197271
         * nested fragments do not retain their instance on configuration changes */
        if (getCurrentFragment() instanceof BrowseOptionsFragment) {
            outState.putParcelable(EXTRAS_OPTIONS_FRAGMENT_SAVED_STATE,
                                   getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment()));
        } else {
            outState.putParcelable(
                    EXTRAS_IMAGE_LIST_FRAGMENT_SAVED_STATE,
                    (getCurrentFragment() instanceof BrowseImageListFragment) ? getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment())
                                                                              : mImageListRetainedState);
        }
    }

    private boolean isRestoredFromSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRAS_SEARCH_OPTIONS)) {
                mCurrentSearchOptions = savedInstanceState.getParcelable(EXTRAS_SEARCH_OPTIONS);
            }
            if (savedInstanceState.containsKey(EXTRAS_IMAGE_LIST_FRAGMENT_SAVED_STATE)) {
                displayImageListFragment(
                        (Fragment.SavedState) savedInstanceState.getParcelable(EXTRAS_IMAGE_LIST_FRAGMENT_SAVED_STATE));
                return true;
            } else if (savedInstanceState.containsKey(EXTRAS_OPTIONS_FRAGMENT_SAVED_STATE)) {
                displayOptionsFragment(
                        (Fragment.SavedState) savedInstanceState.getParcelable(EXTRAS_OPTIONS_FRAGMENT_SAVED_STATE));
                return true;
            }
        }
        return false;
    }

    private void displayImageListFragment(Fragment.SavedState fragmentSavedState) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentLayout, getNewInstanceOfImageListFragment(fragmentSavedState))
                .commit();
        updateActionBarTitleForImageListFragment();
    }

    private void displayOptionsFragment(Fragment.SavedState fragmentSavedState) {
        getChildFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentLayout, getNewInstanceOfOptionsFragment(fragmentSavedState))
                .commit();
        updateActionBarTitleForOptionsFragment();
    }

    private BrowseImageListFragment getNewInstanceOfImageListFragment(Fragment.SavedState fragmentSavedState) {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.EXTRAS_USER, getUser());
        args.putParcelable(EXTRAS_SEARCH_OPTIONS, mCurrentSearchOptions);

        BrowseImageListFragment fragment = new BrowseImageListFragment();
        fragment.setInitialSavedState(fragmentSavedState);
        fragment.setArguments(args);
        return fragment;
    }

    private BrowseOptionsFragment getNewInstanceOfOptionsFragment(Fragment.SavedState fragmentSavedState) {
        /* create a deep copy of current search options so
         * that the new object can be compared with the existing one later */
        DerpibooruSearchOptions options = DerpibooruSearchOptions.copyFrom(mCurrentSearchOptions);
        Bundle args = new Bundle();
        args.putParcelable(EXTRAS_SEARCH_OPTIONS, options);

        BrowseOptionsFragment fragment = new BrowseOptionsFragment();
        fragment.setInitialSavedState(fragmentSavedState);
        fragment.setArguments(args);
        return fragment;
    }

    private void setSearchOptionsFromImageListType(BrowseImageListFragment.Type type) {
        switch (type) {
            case UserFaved:
                mCurrentSearchOptions.setFavesFilter(DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly);
                break;
            case UserUpvoted:
                mCurrentSearchOptions.setUpvotesFilter(DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly);
                break;
            case UserUploaded:
                mCurrentSearchOptions.setUploadsFilter(DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly);
                break;
            case UserWatched:
                mCurrentSearchOptions.setWatchedTagsFilter(DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.buttonSearch)
    void toggleOptionsFragment() {
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            mImageListRetainedState =
                    getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment());
            displayOptionsFragment(null);
        } else {
            DerpibooruSearchOptions newOptions =
                    ((BrowseOptionsFragment) getCurrentFragment()).getSelectedOptions();
            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (!Objects.equal(mCurrentSearchOptions, newOptions)) {
                mCurrentSearchOptions = newOptions;
                displayImageListFragment(null);
            } else {
                displayImageListFragment(mImageListRetainedState);
            }
        }
    }

    /**
     * Returns true if a nested fragment has been popped from the back stack. Returns false
     * if there are no fragments in the back stack.
     */
    public boolean popChildFragmentManagerBackstack() {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            displayImageListFragment(mImageListRetainedState);
            return true;
        }
        return false;
    }

    private Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentById(R.id.fragmentLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrentFragment() instanceof ImageListFragment) {
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }
        if (isTagSearchRequested(requestCode, data)) {
            mTagSearchRequest = data.getStringExtra(ImageActivity.EXTRAS_TAG_SEARCH_QUERY);
        }
    }

    public static boolean isTagSearchRequested(int activityRequestCode, Intent activityResultData) {
        return (activityRequestCode == ImageListFragment.IMAGE_ACTIVITY_REQUEST_CODE)
                && (activityResultData != null)
                && (activityResultData.hasExtra(ImageActivity.EXTRAS_TAG_SEARCH_QUERY));
    }

    /**
     * Displays an image list displaying search results for the requested tag, if such a request exists.
     *
     * @return {@code true} if the tag search was requested, {@code false} otherwise.
     */
    private boolean isDisplayingTagSearchRequest() {
        if (!mTagSearchRequest.isEmpty()) {
            displayImageListWithSearchResultsForTag(mTagSearchRequest);
            mTagSearchRequest = "";
            return true;
        } else if ((getArguments() != null) && (getArguments().containsKey(ImageActivity.EXTRAS_TAG_SEARCH_QUERY))) {
            displayImageListWithSearchResultsForTag(
                    getArguments().getString(ImageActivity.EXTRAS_TAG_SEARCH_QUERY));
            getArguments().remove(ImageActivity.EXTRAS_TAG_SEARCH_QUERY);
            return true;
        }
        return false;
    }

    private void displayImageListWithSearchResultsForTag(String tag) {
        mCurrentSearchOptions = new DerpibooruSearchOptions();
        mCurrentSearchOptions.setSearchQuery(tag);
        displayImageListFragment(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        isDisplayingTagSearchRequest();
    }
}
