package derpibooru.derpy.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import derpibooru.derpy.R;

public class FiltersActivity extends NavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setTitle(R.string.activity_filters);
        initializeNavigationDrawer();

        ((TextView) findViewById(R.id.textFiltersHelp))
                .setText(Html.fromHtml(getString(R.string.filters_help)));
    }
}
