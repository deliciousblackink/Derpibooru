package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import derpibooru.derpy.R;

public class SearchOptionsTabFragment extends ImageListTabFragment {
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
        ((Spinner) v.findViewById(R.id.spinnerSortBy)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_sort_direction_options,
                                                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) v.findViewById(R.id.spinnerSortDirection)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_faves_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) v.findViewById(R.id.spinnerFavesFilter)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_upvotes_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) v.findViewById(R.id.spinnerUpvotesFilter)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_uploads_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) v.findViewById(R.id.spinnerUploadsFilter)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_watched_tags_options,
                                                  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) v.findViewById(R.id.spinnerWatchedTagsFilter)).setAdapter(adapter);

        return v;
    }
}
