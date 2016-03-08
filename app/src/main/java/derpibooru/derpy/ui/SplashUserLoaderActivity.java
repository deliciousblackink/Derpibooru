package derpibooru.derpy.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import derpibooru.derpy.R;
import derpibooru.derpy.UserManager;
import derpibooru.derpy.data.server.DerpibooruUser;

public class SplashUserLoaderActivity extends AppCompatActivity {
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUserDataFromServer();
    }

    private void loadUserDataFromServer() {
        mUserManager = new UserManager(this);
        mUserManager.setOnUserRefreshListener(new UserManager.OnUserRefreshListener() {
            @Override
            public void onUserRefreshed(DerpibooruUser user) {
                launchMainActivity(user);
            }

            @Override
            public void onRefreshFailed() {
                getErrorDialog().show();
            }
        });
        mUserManager.refresh();
    }

    private void launchMainActivity(DerpibooruUser userData) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRAS_USER, userData);
        startActivity(intent);
        finish();
    }

    private AlertDialog getErrorDialog() {
        return new AlertDialog.Builder(this)
                .setMessage(R.string.activity_splash_user_loader_request_failed)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mUserManager.refresh();
                    }
                }).create();
    }
}
