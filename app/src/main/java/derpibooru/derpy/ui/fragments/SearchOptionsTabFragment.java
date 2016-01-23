package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;

public class SearchOptionsTabFragment extends Fragment {
    /* TODO: there must be a more graceful way of handling so many similar inputs */
    private Spinner mSortBySpinner;
    private Spinner mSortDirectionSpinner;
    private Spinner mFavesFilterSpinner;
    private Spinner mUpvotesFilterSpinner;
    private Spinner mUploadsFilterSpinner;
    private Spinner mWatchedTagsSpinner;
    private EditText mMinScoreFilterText;
    private EditText mMaxScoreFilterText;

    public SearchOptionsTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_options_tab, container, false);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.search_sort_by_options,
                                                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortBySpinner = (Spinner) v.findViewById(R.id.spinnerSortBy);
        mSortBySpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_sort_direction_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortDirectionSpinner = (Spinner) v.findViewById(R.id.spinnerSortDirection);
        mSortDirectionSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_faves_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFavesFilterSpinner = (Spinner) v.findViewById(R.id.spinnerFavesFilter);
        mFavesFilterSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_upvotes_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUpvotesFilterSpinner = (Spinner) v.findViewById(R.id.spinnerUpvotesFilter);
        mUpvotesFilterSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_uploads_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUploadsFilterSpinner = (Spinner) v.findViewById(R.id.spinnerUploadsFilter);
        mUploadsFilterSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_watched_tags_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWatchedTagsSpinner = (Spinner) v.findViewById(R.id.spinnerWatchedTagsFilter);
        mWatchedTagsSpinner.setAdapter(adapter);

        mMinScoreFilterText = (EditText) v.findViewById(R.id.textMinScore);
        mMaxScoreFilterText = (EditText) v.findViewById(R.id.textMaxScore);

        return v;
    }

    public DerpibooruSearchOptions getSelectedOptions() {
        DerpibooruSearchOptions.SortBy sortBy = getSelectedSortBySpinnerItem();
        DerpibooruSearchOptions.SortDirection sortDirection = getSelectedSortDirectionSpinnerItem();

        DerpibooruSearchOptions.UserPicksFilter favesFilter = getSelectedFavesFilterSpinnerItem();
        DerpibooruSearchOptions.UserPicksFilter upvotesFilter = getSelectedUpvotesFilterSpinnerItem();
        DerpibooruSearchOptions.UserPicksFilter uploadsFilter = getSelectedUploadsFilterSpinnerItem();
        DerpibooruSearchOptions.UserPicksFilter watchedTagsFilter = getSelectedWatchedTagsSpinnerItem();

        Integer minScoreFilter = getMinScoreFilter();
        Integer maxScoreFilter = getMaxScoreFilter();

        return new DerpibooruSearchOptions(sortBy, sortDirection,
                                           favesFilter, upvotesFilter, uploadsFilter, watchedTagsFilter,
                                           minScoreFilter, maxScoreFilter);
    }

    private DerpibooruSearchOptions.SortBy getSelectedSortBySpinnerItem() {
        switch (mSortBySpinner.getSelectedItem().toString()) {
            case "Creation date":
                return DerpibooruSearchOptions.SortBy.CreatedAt;
            case "Score":
                return DerpibooruSearchOptions.SortBy.Score;
            case "Relevance":
                return DerpibooruSearchOptions.SortBy.Relevance;
            case "Width":
                return DerpibooruSearchOptions.SortBy.Width;
            case "Height":
                return DerpibooruSearchOptions.SortBy.Height;
            case "Comments":
                return DerpibooruSearchOptions.SortBy.Comments;
            case "Random!":
                return DerpibooruSearchOptions.SortBy.Random;
        }
        return DerpibooruSearchOptions.SortBy.CreatedAt;
    }

    private DerpibooruSearchOptions.SortDirection getSelectedSortDirectionSpinnerItem() {
        switch (mSortDirectionSpinner.getSelectedItem().toString()) {
            case "Descending":
                return DerpibooruSearchOptions.SortDirection.Descending;
            case "Ascending":
                return DerpibooruSearchOptions.SortDirection.Ascending;
        }
        return DerpibooruSearchOptions.SortDirection.Descending;
    }

    private DerpibooruSearchOptions.UserPicksFilter getSelectedFavesFilterSpinnerItem() {
        switch (mFavesFilterSpinner.getSelectedItem().toString()) {
            case "No":
                return DerpibooruSearchOptions.UserPicksFilter.No;
            case "Show my faves only":
                return DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly;
            case "Don't show my faves":
                return DerpibooruSearchOptions.UserPicksFilter.NoUserPicks;
        }
        return DerpibooruSearchOptions.UserPicksFilter.No;
    }

    private DerpibooruSearchOptions.UserPicksFilter getSelectedUpvotesFilterSpinnerItem() {
        switch (mUpvotesFilterSpinner.getSelectedItem().toString()) {
            case "No":
                return DerpibooruSearchOptions.UserPicksFilter.No;
            case "Show my upvotes only":
                return DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly;
            case "Don't show my upvotes":
                return DerpibooruSearchOptions.UserPicksFilter.NoUserPicks;
        }
        return DerpibooruSearchOptions.UserPicksFilter.No;
    }

    private DerpibooruSearchOptions.UserPicksFilter getSelectedUploadsFilterSpinnerItem() {
        switch (mUploadsFilterSpinner.getSelectedItem().toString()) {
            case "No":
                return DerpibooruSearchOptions.UserPicksFilter.No;
            case "Show my uploads only":
                return DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly;
            case "Don't show my uploads":
                return DerpibooruSearchOptions.UserPicksFilter.NoUserPicks;
        }
        return DerpibooruSearchOptions.UserPicksFilter.No;
    }

    private DerpibooruSearchOptions.UserPicksFilter getSelectedWatchedTagsSpinnerItem() {
        switch (mWatchedTagsSpinner.getSelectedItem().toString()) {
            case "No":
                return DerpibooruSearchOptions.UserPicksFilter.No;
            case "Show my watched tags only":
                return DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly;
            case "Don't show my watched tags":
                return DerpibooruSearchOptions.UserPicksFilter.NoUserPicks;
        }
        return DerpibooruSearchOptions.UserPicksFilter.No;
    }

    private Integer getMinScoreFilter() {
        String input = mMinScoreFilterText.getText().toString();
        if (!input.equals("")) {
            return Integer.parseInt(input);
        }
        return null;
    }

    private Integer getMaxScoreFilter() {
        String input = mMaxScoreFilterText.getText().toString();
        if (!input.equals("")) {
            return Integer.parseInt(input);
        }
        return null;
    }
}
