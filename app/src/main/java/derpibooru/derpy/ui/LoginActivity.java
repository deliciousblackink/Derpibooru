package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.LoginRequester;

public class LoginActivity extends AppCompatActivity {
    private static final int MIN_ACCEPTED_PASSWORD_LENGTH = 6;

    private EditText mEmailText;
    private EditText mPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailText = ((EditText) findViewById(R.id.textEmail));
        mPasswordText = ((EditText) findViewById(R.id.textPassword));

        ((TextInputLayout) findViewById(R.id.inputEmail))
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
        ((TextInputLayout) findViewById(R.id.inputPassword))
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));

        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputDataAndLogin();
            }
        });
    }

    /* Respond to ActionBar's Up (Back) button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void validateInputDataAndLogin() {
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        if (!isEmailValid(email)) {
            showSnackbar(R.string.activity_login_invalid_email);
            return;
        }
        if (password.length() < MIN_ACCEPTED_PASSWORD_LENGTH) {
            showSnackbar(R.string.activity_login_invalid_password);
            return;
        }
        DerpibooruLoginForm credentials =
                new DerpibooruLoginForm(email, password, ((CheckBox) findViewById(R.id.checkRememberMe)).isChecked());
        loginWithCredentials(credentials);
        showProgressBar();
    }

    private void loginWithCredentials(DerpibooruLoginForm credentials) {
        new LoginRequester(this, credentials, new QueryHandler<Boolean>() {
            @Override
            public void onQueryExecuted(Boolean result) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onQueryFailed() {
                hideProgressBar();
                showSnackbar(R.string.activity_login_failed_request);
            }
        }).fetch();
    }

    private void showProgressBar() {
        findViewById(R.id.progressLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonLogin).setVisibility(View.GONE);

        mEmailText.setEnabled(false);
        mPasswordText.setEnabled(false);
        findViewById(R.id.checkRememberMe).setEnabled(false);

        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(mEmailText.getWindowToken(), 0);
            inputManager.hideSoftInputFromWindow(mPasswordText.getWindowToken(), 0);
        }
    }

    private void hideProgressBar() {
        findViewById(R.id.progressLogin).setVisibility(View.GONE);
        findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);

        mEmailText.setEnabled(true);
        mPasswordText.setEnabled(true);
        findViewById(R.id.checkRememberMe).setEnabled(true);
    }

    private void showSnackbar(int stringResId) {
        Snackbar.make(findViewById(R.id.viewLogin), getString(stringResId), Snackbar.LENGTH_LONG).show();
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
