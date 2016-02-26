package derpibooru.derpy.ui.views;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.storage.SearchHistoryStorage;
import derpibooru.derpy.ui.MainActivity;
import derpibooru.derpy.ui.SearchResultActivity;

/**
 * A floating search view that, unlike AppCompat's SearchView, works
 * outside the AppBar and does not require any XML.
 */
public class FloatingSearchView extends FrameLayout {
    private static final int ICON_SEARCH = R.drawable.ic_search_white_24dp;
    private static final int ICON_BACK = R.drawable.ic_arrow_back_white_24dp;

    private FloatingSearchViewListener mListener;

    @Bind(R.id.buttonSearch) ImageView buttonSearch;
    @Bind(R.id.textSearch) AutoCompleteTextView textSearch;

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

        buttonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusOnTextView(!textSearch.hasFocus());
            }
        });

        textSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setFocusOnTextView(hasFocus);
            }
        });

        SearchHistoryStorage s = new SearchHistoryStorage(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.view_floating_search_recent_item, s.getSearchHistory());
        textSearch.setAdapter(adapter);

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

    private void setFocusOnTextView(boolean focus) {
        if (focus) {
            buttonSearch.setImageDrawable(ContextCompat.getDrawable(getContext(), ICON_BACK));
            textSearch.requestFocus();
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(textSearch, InputMethodManager.SHOW_IMPLICIT);
        } else {
            buttonSearch.setImageDrawable(ContextCompat.getDrawable(getContext(), ICON_SEARCH));
            getRootView().requestFocus();
            InputMethodManager inputManager =
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(textSearch.getWindowToken(), 0);
            }
        }
    }

    private void initiateSearchAction(String query) {
        if (mListener != null) {
            mListener.onSearchAction(query);
        }
    }

    /* TODO: Handle hardware Back button */

    public void setFloatingSearchViewListener(FloatingSearchViewListener listener) {
        mListener = listener;
    }

    public interface FloatingSearchViewListener {
        void onSearchAction(String query);
    }
}
