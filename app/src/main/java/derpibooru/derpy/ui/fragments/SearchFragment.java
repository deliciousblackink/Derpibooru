package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.ui.SearchResultActivity;
import derpibooru.derpy.ui.views.FloatingSearchView;

public class SearchFragment extends Fragment {
    @Bind(R.id.floatingSearchView) private FloatingSearchView mSearchView;
    @Bind(R.id.textSearchHelp) private TextView mHelpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(v);
        initializeSearchView();
        initializeHelpView();
        return v;
    }

    private void initializeSearchView() {
        mSearchView.setSearchResultActivity(SearchResultActivity.class);
        mSearchView.setOnLayoutSizeChangedListener(new FloatingSearchView.OnLayoutSizeChangedListener() {
            @Override
            public void onSizeChanged() {
                mSearchView.findViewById(R.id.topLayout).requestLayout();
            }
        });
    }

    private void initializeHelpView() {
        mHelpView.setText(Html.fromHtml(getString(R.string.search_help)));
    }
}
