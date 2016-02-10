package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

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
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mSelectedOptions.setMinScore(getInt(s.toString()));
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });
        ((EditText) parent.findViewById(R.id.textMaxScore))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mSelectedOptions.setMaxScore(getInt(s.toString()));
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });
    }

    private Integer getInt(String text) {
        return (!text.equals("")) ? Integer.parseInt(text) : null;
    }
}
