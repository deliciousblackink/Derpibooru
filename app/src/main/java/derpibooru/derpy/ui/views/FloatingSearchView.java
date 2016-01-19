package derpibooru.derpy.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.RecentSearchListAdapter;

/**
 * A floating search view that, unlike AppCompat's SearchView, works
 * outside the AppBar and does not require any XML.
 */
public class FloatingSearchView extends LinearLayout {
    private static final int ICON_SEARCH = R.drawable.ic_search_white_24dp;
    private static final int ICON_BACK = R.drawable.ic_arrow_back_white_24dp;

    private Activity mSearchResultActivity;
    private RecyclerView mRecentSearchView;
    private View mRecentSearchDivider;
    private ImageView mSearchButton;
    private EditText mSearchText;

    private RecentSearchListAdapter mAdapter;

    private boolean isExtended = false;

    public FloatingSearchView(Context context) {
        super(context);
        init();
    }

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
        addView(view);

        mRecentSearchDivider = findViewById(R.id.viewRecentSearchDivider);
        mRecentSearchView = (RecyclerView) findViewById(R.id.viewRecentSearch);

        mRecentSearchView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecentSearchView.setLayoutManager(mLayoutManager);
        mAdapter = new RecentSearchListAdapter(getContext());
        mRecentSearchView.setAdapter(mAdapter);

        mSearchButton = (ImageView) findViewById(R.id.buttonSearch);
        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExtended) {
                    expandSearch();
                } else {
                    collapseSearch();
                }
            }
        });

        mSearchText =  (EditText) findViewById(R.id.textSearch);
        mSearchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !isExtended) {
                    expandSearch();
                } else if (!hasFocus && isExtended) {
                    collapseSearch();
                }
            }
        });

        collapseSearch();
    }

    /* TODO: Handle hardware Back button */

    private void collapseSearch() {
        isExtended = false;
        mSearchButton.setImageDrawable(ContextCompat.getDrawable(getContext(), ICON_SEARCH));
        mRecentSearchView.setVisibility(View.GONE);
        mRecentSearchDivider.setVisibility(View.GONE);
        findViewById(R.id.linearLayout).requestFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    private void expandSearch() {
        isExtended = true;
        mSearchButton.setImageDrawable(ContextCompat.getDrawable(getContext(), ICON_BACK));
        mRecentSearchView.setVisibility(View.VISIBLE);
        mRecentSearchDivider.setVisibility(View.VISIBLE);
        mSearchText.requestFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mSearchText, InputMethodManager.SHOW_IMPLICIT);
        /* play item animations */
        mAdapter.notifyDataSetChanged();
    }

    public void setSearchResultActivity(Activity searchResultActivity) {
        mSearchResultActivity = searchResultActivity;

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            return true;
                 }
                 return false;
            }});
    }
}
