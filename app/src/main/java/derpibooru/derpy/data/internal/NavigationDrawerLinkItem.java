package derpibooru.derpy.data.internal;

public class NavigationDrawerLinkItem extends NavigationDrawerItem {
    private final int mLinkNavigationViewItemId;

    public NavigationDrawerLinkItem(int navigationViewItemId, NavigationDrawerItem target) {
        super(target);
        mLinkNavigationViewItemId = navigationViewItemId;
    }

    public int getLinkNavigationViewItemId() {
        return mLinkNavigationViewItemId;
    }
}
