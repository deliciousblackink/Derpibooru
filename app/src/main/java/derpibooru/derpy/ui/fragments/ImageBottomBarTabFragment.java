package derpibooru.derpy.ui.fragments;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;
import derpibooru.derpy.ui.views.FlowLayout;

public abstract class ImageBottomBarTabFragment extends Fragment {
    private ImageBottomBarTabAdapter.ImageBottomBarTabHandler mContentHeightHandler;
    private ViewGroup mRootViewGroup;

    public ImageBottomBarTabFragment() {
        super();
    }

    public void setContentHeightHandler(ImageBottomBarTabAdapter.ImageBottomBarTabHandler handler) {
        mContentHeightHandler = handler;
    }

    public void provideCurrentContentHeight(ImageBottomBarTabAdapter.ImageBottomBarTab tab) {
        if (mContentHeightHandler != null) {
            mRootViewGroup.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new LayoutHeightHandler(tab));
        }
    }

    protected void setRootViewGroup(ViewGroup root) {
        mRootViewGroup = root;
    }

    private class LayoutHeightHandler implements ViewTreeObserver.OnGlobalLayoutListener {
        private ImageBottomBarTabAdapter.ImageBottomBarTab mTab;

        public LayoutHeightHandler(ImageBottomBarTabAdapter.ImageBottomBarTab tab) {
            mTab = tab;
        }

        @Override
        public void onGlobalLayout() {
            int height = 0;
            for (int x = 0; x < mRootViewGroup.getChildCount(); x++) {
                /* TODO: FlowLayout does not provide correct measurements
                 using a temp variable hack atm */
                height += (mRootViewGroup.getChildAt(x) instanceof FlowLayout) ?
                          ((FlowLayout) mRootViewGroup.getChildAt(x)).getOnMeasureHeight()
                          : mRootViewGroup.getChildAt(x).getMeasuredHeight();
            }
            mContentHeightHandler.onTabHeightProvided(mTab, height);

            ViewTreeObserver obs = mRootViewGroup.getViewTreeObserver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                obs.removeOnGlobalLayoutListener(this);
            } else {
                obs.removeGlobalOnLayoutListener(this);
            }
        }
    }
}
