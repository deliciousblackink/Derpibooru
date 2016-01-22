package derpibooru.derpy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.FloatingSearchView;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingSearchView search = (FloatingSearchView) findViewById(R.id.floatingSearch);
        search.setSearchResultActivity(SearchResultActivity.class);
        search.setOnLayoutSizeChangedListener(new FloatingSearchView.OnLayoutSizeChangedListener() {
            @Override
            public void onSizeChanged() {
                findViewById(R.id.topLayout).requestLayout();
            }
        });

        ((TextView) findViewById(R.id.textSearchHelp))
                .setText(Html.fromHtml(getString(R.string.search_help)));

    }

    /* Respond to ActionBar's Up (Back) button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
