package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;

public class SearchOptionsTabFragment extends Fragment {
    private DerpibooruSearchOptions mSelectedOptions = new DerpibooruSearchOptions();

    public SearchOptionsTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_options_tab, container, false);
        initSpinnerListeners(v);
        setTextListeners(v);
        return v;
    }

    public DerpibooruSearchOptions getSelectedOptions() {
        return mSelectedOptions;
    }

    private void initSpinnerListeners(View v) {
        ((Spinner) v.findViewById(R.id.spinnerSortBy)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setSortBy(DerpibooruSearchOptions.SortBy.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ((Spinner) v.findViewById(R.id.spinnerSortDirection)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setSortDirection(DerpibooruSearchOptions.SortDirection.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ((Spinner) v.findViewById(R.id.spinnerFavesFilter)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setFavesFilter(DerpibooruSearchOptions.UserPicksFilter.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ((Spinner) v.findViewById(R.id.spinnerUpvotesFilter)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setUpvotesFilter(DerpibooruSearchOptions.UserPicksFilter.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ((Spinner) v.findViewById(R.id.spinnerUploadsFilter)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setUpvotesFilter(DerpibooruSearchOptions.UserPicksFilter.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        ((Spinner) v.findViewById(R.id.spinnerWatchedTagsFilter)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOptions.setWatchedTagsFilter(DerpibooruSearchOptions.UserPicksFilter.fromValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setTextListeners(View parent) {
        ((EditText) parent.findViewById(R.id.textMinScore))
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            mSelectedOptions.setMinScore(getInt(v));
                            return true;
                        }
                        return false;
                    }
                });
        ((EditText) parent.findViewById(R.id.textMaxScore))
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            mSelectedOptions.setMaxScore(getInt(v));
                            return true;
                        }
                        return false;
                    }
                });
    }

    private Integer getInt(TextView text) {
        String input = text.getText().toString();
        if (!input.equals("")) {
            return Integer.parseInt(input);
        }
        return null;
    }
}
