package derpibooru.derpy.data.server;

public class DerpibooruLoginForm {
    private String mEmail;
    private String mPassword;
    private boolean mRememberUser;

    public DerpibooruLoginForm(String email, String password,
                               boolean rememberUser) {
        mEmail = email;
        mPassword = password;
        mRememberUser = rememberUser;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public boolean isUserToBeRemembered() {
        return mRememberUser;
    }
}
