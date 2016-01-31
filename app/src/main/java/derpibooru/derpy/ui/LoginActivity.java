package derpibooru.derpy.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruLoginForm;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.User;

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
                login();
            }
        });

        ((ProgressBar) findViewById(R.id.progressLogin)).getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
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

    private void showSnackbar(String content) {
        Snackbar.make(findViewById(R.id.viewLogin),
                      content, Snackbar.LENGTH_LONG).show();
    }

    private void login() {
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        if (!isEmailValid(email)) {
            showSnackbar("Invalid e-mail");
            return;
        }
        if (password.length() < MIN_ACCEPTED_PASSWORD_LENGTH) {
            showSnackbar("The password is too short");
            return;
        }

        DerpibooruLoginForm form =
                new DerpibooruLoginForm(email, password,
                                        ((CheckBox) findViewById(R.id.checkRememberMe)).isChecked());

        User user = new User(this, new User.UserRequestHandler() {
            @Override
            public void onUserDataObtained(DerpibooruUser userData) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onFailedLogin() {
                hideProgressBar();
                showSnackbar("Invalid e-mail or password");
            }

            @Override
            public void onNetworkError() {
                hideProgressBar();
                showSnackbar("An error occurred, please try again later");
            }

            @Override
            public void onFailedLogout() { }
        });
        user.login(form);
        showProgressBar();
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

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
