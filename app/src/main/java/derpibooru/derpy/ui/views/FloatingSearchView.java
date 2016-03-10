package derpibooru.derpy.ui.views;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
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

    private SearchHistoryStorage mHistory;

    public FloatingSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        textSearch.setOnEditorActionListener(listener);
    }

    public Editable getText() {
        return textSearch.getText();
    }

    public void setText(CharSequence text) {
        textSearch.setText(text);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_floating_search, null);
        ButterKnife.bind(this, view);
        addView(view);
        initializeSearchHistory();
    }

    private void initializeSearchHistory() {
        mHistory = new SearchHistoryStorage(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.view_floating_search_recent_item, mHistory.getSearchHistory());
        textSearch.setAdapter(adapter);
    }
}
