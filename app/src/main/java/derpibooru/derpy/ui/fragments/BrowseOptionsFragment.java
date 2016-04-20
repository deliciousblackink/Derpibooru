package derpibooru.derpy.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.storage.SearchHistoryStorage;

public class BrowseOptionsFragment extends Fragment {
    @BindColor(R.color.colorTextDark) int textColor;
    @BindColor(R.color.colorAccent) int accentColor;

    @Bind(R.id.textSearch) AutoCompleteTextView searchText;
    @Bind(R.id.spinnerSortBy) Spinner sortBy;
    @Bind(R.id.spinnerSortDirection) Spinner sortDirection;
    @Bind(R.id.spinnerFavesFilter) Spinner favesFilter;
    @Bind(R.id.spinnerUpvotesFilter) Spinner upvotesFilter;
    @Bind(R.id.spinnerUploadsFilter) Spinner uploadsFilter;
    @Bind(R.id.spinnerWatchedTagsFilter) Spinner watchedTagsFilter;
    @Bind(R.id.textMinScore) EditText minScore;
    @Bind(R.id.textMaxScore) EditText maxScore;

    private SearchHistoryStorage mHistory;

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
        setSearchTextHistoryAdapter();
        setSpinnerTintToggleListeners();
        setSpinnerState(mSelectedOptions);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BrowseFragment.EXTRAS_SEARCH_OPTIONS, getSelectedOptions());
    }

    public DerpibooruSearchOptions getSelectedOptions() {
        if (searchText != null) {
            hideSoftKeyboard();
            mSelectedOptions.setSearchQuery(
                    searchText.getText().toString().isEmpty() ? "*" : searchText.getText().toString());
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

            mHistory.addSearchQuery(mSelectedOptions.getSearchQuery());
        }
        return mSelectedOptions;
    }

    private void setSpinnerState(DerpibooruSearchOptions from) {
        if (!from.getSearchQuery().equals("*")) searchText.setText(from.getSearchQuery());
        sortBy.setSelection(from.getSortBy().toValue());
        sortDirection.setSelection(from.getSortDirection().toValue());
        favesFilter.setSelection(from.getFavesFilter().toValue());
        upvotesFilter.setSelection(from.getUpvotesFilter().toValue());
        uploadsFilter.setSelection(from.getUploadsFilter().toValue());
        watchedTagsFilter.setSelection(from.getWatchedTagsFilter().toValue());
        if (from.getMinScore() != null) minScore.setText(from.getMinScore().toString());
        if (from.getMaxScore() != null) maxScore.setText(from.getMaxScore().toString());
    }

    private void setSpinnerTintToggleListeners() {
        AdapterView.OnItemSelectedListener toggleListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (view != null) {
                            ((TextView) view).setTextColor((position == 0) ? textColor : accentColor);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                };
        sortBy.setOnItemSelectedListener(toggleListener);
        sortDirection.setOnItemSelectedListener(toggleListener);
        favesFilter.setOnItemSelectedListener(toggleListener);
        upvotesFilter.setOnItemSelectedListener(toggleListener);
        uploadsFilter.setOnItemSelectedListener(toggleListener);
        watchedTagsFilter.setOnItemSelectedListener(toggleListener);
    }

    private Integer getInteger(String text) {
        return (text.equals("")) ? null : Integer.parseInt(text);
    }

    private void setSearchTextHistoryAdapter() {
        mHistory = new SearchHistoryStorage(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.view_search_history_item, mHistory.getSearchHistory());
        searchText.setAdapter(adapter);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }
}
