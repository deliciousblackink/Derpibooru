package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.storage.SearchHistoryStorage;

/**
 * A floating search view that, unlike AppCompat's SearchView, works
 * outside the AppBar and does not require any XML.
 */
public class FloatingSearchView extends FrameLayout {
    @Bind(R.id.textSearch) AutoCompleteTextView textSearch;

    private FloatingSearchViewListener mListener;
    private SearchHistoryStorage mHistory;

    public FloatingSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_floating_search, null);
        ButterKnife.bind(this, view);
        addView(view);
        initializeSearchHistory();
        initializeSearchActionListener();
    }

    private void initializeSearchHistory() {
        mHistory = new SearchHistoryStorage(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.view_floating_search_recent_item, mHistory.getSearchHistory());
        textSearch.setAdapter(adapter);
    }

    private void initializeSearchActionListener() {
        textSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    initiateSearchAction(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void initiateSearchAction(String query) {
        mHistory.addSearchQuery(query);
        if (mListener != null) {
            mListener.onSearchAction(query);
        }
    }

    public void setFloatingSearchViewListener(FloatingSearchViewListener listener) {
        mListener = listener;
    }

    public interface FloatingSearchViewListener {
        void onSearchAction(String query);
    }
}
