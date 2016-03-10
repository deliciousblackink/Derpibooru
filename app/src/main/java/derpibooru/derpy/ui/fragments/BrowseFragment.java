package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.common.base.Objects;

import org.w3c.dom.ProcessingInstruction;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.server.providers.SearchProvider;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.fragments.tabs.MainActivityNewImagesTabFragment;

public class BrowseFragment extends NavigationDrawerUserFragment {
    private static final String EXTRAS_NESTED_FRAGMENT_STATE = "derpibooru.derpy.NestedState";
    private static final String EXTRAS_SEARCH_OPTIONS = "derpibooru.derpy.SearchOptions";

    private DerpibooruSearchOptions mCurrentSearchOptions = new DerpibooruSearchOptions();
    private Fragment.SavedState mImageListRetainedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse, container, false);
        ButterKnife.bind(this, v);
        initializeImageListFragment(savedInstanceState);
        return v;
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser newUser) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* https://code.google.com/p/android/issues/detail?id=197271
         * nested fragments do not retain their instance on configuration changes */
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            mImageListRetainedState = getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment());
        }
        outState.putParcelable(EXTRAS_NESTED_FRAGMENT_STATE, mImageListRetainedState);
    }

    private void initializeImageListFragment(Bundle savedInstanceState) {
        if ((savedInstanceState != null)
                && (savedInstanceState.containsKey(EXTRAS_NESTED_FRAGMENT_STATE))) {
            initializeImageListFragment((Fragment.SavedState) savedInstanceState.getParcelable(EXTRAS_NESTED_FRAGMENT_STATE));
        } else {
            initializeImageListFragment((Fragment.SavedState) null);
        }
    }

    private void initializeImageListFragment(Fragment.SavedState fragmentSavedState) {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.EXTRAS_USER, getUser());
        args.putParcelable(EXTRAS_SEARCH_OPTIONS, mCurrentSearchOptions);
        BrowseImageListFragment fragment = new BrowseImageListFragment();
        fragment.setInitialSavedState(fragmentSavedState);
        fragment.setArguments(args);
        displayImageListFragment(fragment);
    }

    private void displayImageListFragment(BrowseImageListFragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentLayout, fragment)
                .commit();
    }

    @OnClick(R.id.buttonSearch)
    void openOptionsFragment() {
        if (getCurrentFragment() instanceof BrowseImageListFragment) {
            mImageListRetainedState =
                    getChildFragmentManager().saveFragmentInstanceState(getCurrentFragment());
            getChildFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentLayout, new BrowseOptionsFragment())
                    .commit();
        } else {
            DerpibooruSearchOptions newOptions =
                    ((BrowseOptionsFragment) getCurrentFragment()).getSelectedOptions();
            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            initializeImageListFragment(mImageListRetainedState);
            /*if (!Objects.equal(mCurrentSearchOptions, newOptions)) {
                /* create a deep copy of search options */
                /*mCurrentSearchOptions = DerpibooruSearchOptions.copyFrom(newOptions);
                initializeImageListFragment(null);
            }*/
        }
    }

    /**
     * Returns true if a nested fragment has been popped from the back stack. Returns false
     * if there are no fragments in the back stack.
     */
    public boolean popChildFragmentManagerBackstack() {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            initializeImageListFragment(mImageListRetainedState);
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
