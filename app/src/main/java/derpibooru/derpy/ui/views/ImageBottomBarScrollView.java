package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * @see <a href="http://wiresareobsolete.com/2011/09/synchronizing-scrollview/">Implementation details</a>
 */
public class ImageBottomBarScrollView extends ScrollView {
    private View mAnchorView;
    private View mHeaderView;

    private int mScrollLimitMin = 0;

    public ImageBottomBarScrollView(Context context) {
        super(context);
    }

    public ImageBottomBarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBottomBarScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageBottomBarScrollView setAnchorViewForStickyHeader(View v) {
        mAnchorView = v;
        syncViews();
        return this;
    }

    public ImageBottomBarScrollView setStickyHeaderView(View v) {
        mHeaderView = v;
        syncViews();
        return this;
    }

    public ImageBottomBarScrollView setMinScrollLimit(int limit) {
        mScrollLimitMin = limit;
        return this;
    }

    public int getMinScrollLimit() {
        return mScrollLimitMin;
    }

    private void syncViews() {
        if (mAnchorView == null || mHeaderView == null) {
            return;
        }

        int distance = mAnchorView.getTop() - mHeaderView.getTop();
        mHeaderView.offsetTopAndBottom(distance);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        syncViews();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t < mScrollLimitMin) {
            super.scrollTo(0, mScrollLimitMin);
            return;
        }

        super.onScrollChanged(l, t, oldl, oldt);
        if (mAnchorView == null || mHeaderView == null) {
            return;
        }

        int matchDistance = mAnchorView.getTop() - getScrollY();
        int offset = getScrollY() - mHeaderView.getTop();
        if (matchDistance < 0) {
            mHeaderView.offsetTopAndBottom(offset);
        } else {
            syncViews();
        }
    }
}
