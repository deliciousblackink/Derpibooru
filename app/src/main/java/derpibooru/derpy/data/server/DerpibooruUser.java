package derpibooru.derpy.data.server;

public class DerpibooruUser {
    private boolean mIsLoggedIn;
    private String mUsername;
    private String mAvatarUrl;
    private DerpibooruFilter mCurrentFilter;

    public DerpibooruUser(String username, String avatarUrl) {
        mUsername = username;
        mAvatarUrl = "https:" + avatarUrl;
        mIsLoggedIn = (!username.equals(""));
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public DerpibooruFilter getCurrentFilter() {
        return mCurrentFilter;
    }

    public DerpibooruUser setCurrentFilter(DerpibooruFilter newFilter) {
        mCurrentFilter = newFilter;
        return this;
    }
}