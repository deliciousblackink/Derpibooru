package derpibooru.derpy.ui;

import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import derpibooru.derpy.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextInputLayout) findViewById(R.id.inputEmail))
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));

        ((TextInputLayout) findViewById(R.id.inputPassword))
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
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
