package derpibooru.derpy.data.server;

public class DerpibooruUser {
    private boolean mIsLoggedIn;
    private String mUsername;
    private DerpibooruFilter mCurrentFilter;

    public DerpibooruUser(String username) {
        mUsername = username;
        mIsLoggedIn = (!username.equals(""));
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public String getUsername() {
        return mUsername;
    }

    public DerpibooruFilter getCurrentFilter() {
        return mCurrentFilter;
    }

    public DerpibooruUser setCurrentFilter(DerpibooruFilter newFilter) {
        mCurrentFilter = newFilter;
        return this;
    }
}