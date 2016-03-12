package derpibooru.derpy.data.server;

public class DerpibooruLoginForm {
    private final String mEmail;
    private final String mPassword;
    private final boolean mRememberUser;

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
