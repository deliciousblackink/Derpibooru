package derpibooru.derpy.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import derpibooru.derpy.R;

public class SearchResultActivity extends AppCompatActivity {
    /* TODO: should be a singleTop activity
     * http://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            /* TODO: implement search */
        }
    }
}
