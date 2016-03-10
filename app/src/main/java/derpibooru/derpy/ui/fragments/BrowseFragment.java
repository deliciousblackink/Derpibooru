package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.MainActivity;

public class BrowseFragment extends NavigationDrawerUserFragment {
    public static final String EXTRAS_SEARCH_OPTIONS = "derpibooru.derpy.SearchOptions";
    private static final String EXTRAS_NESTED_FRAGMENT_STATE = "derpibooru.derpy.NestedState";

    private DerpibooruSearchOptions mCurrentSearchOptions = new DerpibooruSearchOptions();
    private Fragment.SavedState mImageListRetainedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse, container, false);
        ButterKnife.bind(this, v);
        if ((savedInstanceState != null)
                && (savedInstanceState.containsKey(EXTRAS_SEARCH_OPTIONS))) {
            mCurrentSearchOptions = savedInstanceState.getParcelable(EXTRAS_SEARCH_OPTIONS);
        }
        displayImageListFragment(savedInstanceState);
        return v;
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser newUser) {
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            ((BrowseImageListFragment) getCurrentFragment()).onUserRefreshed(newUser);
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
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            mImageListRetainedState =
                    getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment());
        }
        outState.putParcelable(EXTRAS_NESTED_FRAGMENT_STATE, mImageListRetainedState);
    }

    private void displayImageListFragment(Bundle savedInstanceState) {
        if ((savedInstanceState != null)
                && (savedInstanceState.containsKey(EXTRAS_NESTED_FRAGMENT_STATE))) {
            displayImageListFragment((Fragment.SavedState) savedInstanceState.getParcelable(EXTRAS_NESTED_FRAGMENT_STATE));
        } else {
            displayImageListFragment((Fragment.SavedState) null);
        }
    }

    private void displayImageListFragment(Fragment.SavedState fragmentSavedState) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentLayout, getNewInstanceOfImageListFragment(fragmentSavedState))
                .commit();
    }

    private void displayOptionsFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentLayout, getNewInstanceOfOptionsFragment())
                .commit();
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

    private BrowseOptionsFragment getNewInstanceOfOptionsFragment() {
        /* create a deep copy of current search options so
         * that the new object can be compared with the existing one later */
        DerpibooruSearchOptions options = DerpibooruSearchOptions.copyFrom(mCurrentSearchOptions);
        Bundle args = new Bundle();
        args.putParcelable(EXTRAS_SEARCH_OPTIONS, options);

        BrowseOptionsFragment fragment = new BrowseOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.buttonSearch)
    void toggleOptionsFragment() {
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            mImageListRetainedState =
                    getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment());
            displayOptionsFragment();
        } else {
            DerpibooruSearchOptions newOptions =
                    ((BrowseOptionsFragment) getCurrentFragment()).getSelectedOptions();
            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (!Objects.equal(mCurrentSearchOptions, newOptions)) {
                mCurrentSearchOptions = newOptions;
                displayImageListFragment((Fragment.SavedState) null);
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

    public static class BrowseImageListFragment extends ImageListFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getArguments().containsKey(EXTRAS_NESTED_FRAGMENT_STATE)) {
                savedInstanceState = getArguments().getBundle(EXTRAS_NESTED_FRAGMENT_STATE);
            }
            View v = super.onCreateView(inflater, container, savedInstanceState);
            super.initializeList(
                    new SearchProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler())
                            .searching((DerpibooruSearchOptions)
                                               getArguments().getParcelable(EXTRAS_SEARCH_OPTIONS)));
            return v;
        }
    }
}
