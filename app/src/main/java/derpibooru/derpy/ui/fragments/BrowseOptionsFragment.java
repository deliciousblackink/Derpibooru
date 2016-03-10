package derpibooru.derpy.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.ui.views.FloatingSearchView;

public class BrowseOptionsFragment extends Fragment {
    @Bind(R.id.floatingSearchView) FloatingSearchView searchQuery;

    @Bind(R.id.spinnerSortBy) Spinner sortBy;
    @Bind(R.id.spinnerSortDirection) Spinner sortDirection;
    @Bind(R.id.spinnerFavesFilter) Spinner favesFilter;
    @Bind(R.id.spinnerUpvotesFilter) Spinner upvotesFilter;
    @Bind(R.id.spinnerUploadsFilter) Spinner uploadsFilter;
    @Bind(R.id.spinnerWatchedTagsFilter) Spinner watchedTagsFilter;

    @Bind(R.id.textMinScore) EditText minScore;
    @Bind(R.id.textMaxScore) EditText maxScore;

    private DerpibooruSearchOptions mSelectedOptions = new DerpibooruSearchOptions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse_options, container, false);
        ButterKnife.bind(this, v);
        if ((savedInstanceState != null)
                && (savedInstanceState.containsKey(BrowseFragment.EXTRAS_SEARCH_OPTIONS))) {
            mSelectedOptions = savedInstanceState.getParcelable(BrowseFragment.EXTRAS_SEARCH_OPTIONS);
        } else {
            mSelectedOptions = getArguments().getParcelable(BrowseFragment.EXTRAS_SEARCH_OPTIONS);
        }
        setSearchActionListenerForSearchQueryView();
        setSpinnerState(mSelectedOptions);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BrowseFragment.EXTRAS_SEARCH_OPTIONS, getSelectedOptions());
    }

    public DerpibooruSearchOptions getSelectedOptions() {
        hideSoftKeyboard();
        mSelectedOptions.setSearchQuery(
                searchQuery.getText().toString().isEmpty() ? "*" : searchQuery.getText().toString());
        mSelectedOptions.setSortBy(
                DerpibooruSearchOptions.SortBy.fromValue(sortBy.getSelectedItemPosition()));
        mSelectedOptions.setSortDirection(
                DerpibooruSearchOptions.SortDirection.fromValue(sortDirection.getSelectedItemPosition()));
        mSelectedOptions.setFavesFilter(
                DerpibooruSearchOptions.UserPicksFilter.fromValue(favesFilter.getSelectedItemPosition()));
        mSelectedOptions.setUpvotesFilter(
                DerpibooruSearchOptions.UserPicksFilter.fromValue(upvotesFilter.getSelectedItemPosition()));
        mSelectedOptions.setUploadsFilter(
                DerpibooruSearchOptions.UserPicksFilter.fromValue(uploadsFilter.getSelectedItemPosition()));
        mSelectedOptions.setWatchedTagsFilter(
                DerpibooruSearchOptions.UserPicksFilter.fromValue(watchedTagsFilter.getSelectedItemPosition()));
        mSelectedOptions.setMinScore(
                getInteger(minScore.getText().toString()));
        mSelectedOptions.setMaxScore(
                getInteger(maxScore.getText().toString()));
        return mSelectedOptions;
    }

    private void setSpinnerState(DerpibooruSearchOptions from) {
        if (!from.getSearchQuery().equals("*")) searchQuery.setText(from.getSearchQuery());
        sortBy.setSelection(from.getSortBy().toValue());
        sortDirection.setSelection(from.getSortDirection().toValue());
        favesFilter.setSelection(from.getFavesFilter().toValue());
        upvotesFilter.setSelection(from.getUpvotesFilter().toValue());
        uploadsFilter.setSelection(from.getUploadsFilter().toValue());
        watchedTagsFilter.setSelection(from.getWatchedTagsFilter().toValue());
        if (from.getMinScore() != null) minScore.setText(from.getMinScore().toString());
        if (from.getMaxScore() != null) maxScore.setText(from.getMaxScore().toString());
    }

    private Integer getInteger(String text) {
        return (text.equals("")) ? null : Integer.parseInt(text);
    }

    private void setSearchActionListenerForSearchQueryView() {
        /* TODO: start search on keyboard search button press */
        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
    }
}
