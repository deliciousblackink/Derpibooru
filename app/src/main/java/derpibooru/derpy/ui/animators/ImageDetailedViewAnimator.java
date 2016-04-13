package derpibooru.derpy.ui.animators;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

public class ImageDetailedViewAnimator {
    private static final String EXTRAS_BOTTOM_BAR_EXTENSION_STATE = "derpibooru.derpy.BottomBarExtensionState";
    private static final String EXTRAS_IS_BOTTOM_BAR_HIDDEN = "derpibooru.derpy.IsBottomBarHidden";

    private static final long ANIMATION_HEADER_DELAY = 150L;
    private static final long ANIMATION_DURATION_HEADER = 100L;
    private static final long ANIMATION_DURATION_DETAILED_VIEW_TOGGLE = 200L;
    private static final long ANIMATION_DURATION_BOTTOM_BAR = 200L;

    private final int mHeaderHeight;
    private final int mBottomBarTabPagerMaximumExtension;
    private final int mBottomBarTabPagerHalfExtension;

    private final View mTransparentOverlay;
    private final View mToolbar;
    private final View mTopBarHeader;
    private final View mBottomBarHeader;
    private final View mBottomBarTabPager;

    private BottomBarExtensionState mBottomBarExtensionState = BottomBarExtensionState.None;
    private boolean mIsBottomBarHidden; /* used for slide out animation (user tapping on the image to hide detailed view) */

    public ImageDetailedViewAnimator(View transparentOverlay, View toolbar, View topBarHeader,
                                     View bottomBarHeader, View bottomBarTabPager) {
        mTransparentOverlay = transparentOverlay;
        mToolbar = toolbar;
        mTopBarHeader = topBarHeader;
        mBottomBarHeader = bottomBarHeader;
        mBottomBarTabPager = bottomBarTabPager;

        /* height of bottom bar header = height of top bar = height of toolbar */
        mHeaderHeight = toolbar.getMeasuredHeight();
        /* height of transparent overlay = height of the whole view;
         * height of the whole view - height of toolbar (bottom bar should not overlap it) - height of bottom bar header */
        mBottomBarTabPagerMaximumExtension = transparentOverlay.getMeasuredHeight() - mHeaderHeight - mHeaderHeight;
        mBottomBarTabPagerHalfExtension = mBottomBarTabPagerMaximumExtension / 2;
    }

    public void animate(TransparentOverlayAnimationRunnable animation) {
        if (animation instanceof HeadersExtensionAnimation) {
            /* without delay, the animation appears extremely jerky, probably due to the expensive initializations running at the same time.
             * TODO: there may be a more elegant solution without delay */
            mTransparentOverlay.postDelayed(animation, ANIMATION_HEADER_DELAY);
        } else {
            mTransparentOverlay.post(animation);
        }
    }

    public void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        animate(new HeadersExtensionAnimation(0));
        mBottomBarExtensionState = BottomBarExtensionState.fromValue(savedInstanceState.getInt(EXTRAS_BOTTOM_BAR_EXTENSION_STATE));
        mIsBottomBarHidden = savedInstanceState.getBoolean(EXTRAS_IS_BOTTOM_BAR_HIDDEN);
        if (mIsBottomBarHidden) {
            mIsBottomBarHidden = false;
            animate(new DetailedViewToggleAnimation());
        } else if (mBottomBarExtensionState != BottomBarExtensionState.None) {
            animate(new BottomBarExtensionStateAnimation(mBottomBarExtensionState, 0, true));
        }
    }

    public void saveInstanceState(Bundle outState) {
        outState.putInt(EXTRAS_BOTTOM_BAR_EXTENSION_STATE, mBottomBarExtensionState.toValue());
        outState.putBoolean(EXTRAS_IS_BOTTOM_BAR_HIDDEN, mIsBottomBarHidden);
    }

    public BottomBarExtensionState getBottomBarExtensionState() {
        return mBottomBarExtensionState;
    }

    private void setViewHeight(View target, int height) {
        target.getLayoutParams().height = height;
        target.requestLayout();
    }

    private int getBottomBarTabPagerHeightForState(BottomBarExtensionState targetState) {
        return ((targetState == BottomBarExtensionState.Max)
                ? mBottomBarTabPagerMaximumExtension
                : ((targetState == BottomBarExtensionState.HalfSize)
                   ? mBottomBarTabPagerHalfExtension
                   : 0));
    }

    public class HeadersExtensionAnimation extends TransparentOverlayAnimationRunnable {
        public HeadersExtensionAnimation() {
            this(ANIMATION_DURATION_HEADER);
        }

        HeadersExtensionAnimation(long duration) {
            /* pass the combined height of bottom and top bars;
             * the value is subtracted from the transparent overlay height */
            super(duration, 0, mHeaderHeight * 2);
        }

        @Override
        protected void onAnimationUpdate(int value) {
            value /= 2; /* see the constructor */
            setViewHeight(mTopBarHeader, value);
            setViewHeight(mBottomBarHeader, value);
        }
    }

    public class DetailedViewToggleAnimation extends TransparentOverlayAnimationRunnable {
        public DetailedViewToggleAnimation() {
            /* the animation slides in/out three headers (toolbar, top bar and bottom bar) */
            super(ANIMATION_DURATION_DETAILED_VIEW_TOGGLE,
                  ((mToolbar.getMeasuredHeight() != 0) ? (mHeaderHeight * 3) : 0),
                  ((mToolbar.getMeasuredHeight() != 0) ? 0 : (mHeaderHeight * 3)));

            if (!mIsBottomBarHidden) {
                if (mBottomBarExtensionState != BottomBarExtensionState.None) {
                    animate(new BottomBarExtensionStateAnimation(BottomBarExtensionState.None, ANIMATION_DURATION_BOTTOM_BAR, false));
                }
            } else {
                if (mBottomBarExtensionState != BottomBarExtensionState.None) {
                    animate(new BottomBarExtensionStateAnimation(mBottomBarExtensionState));
                }
            }
            mIsBottomBarHidden = !mIsBottomBarHidden;
        }

        @Override
        protected void onAnimationUpdate(int value) {
            value /= 3;
            setViewHeight(mToolbar, value);
            setViewHeight(mTopBarHeader, value);
            setViewHeight(mBottomBarHeader, value);
        }
    }

    public class BottomBarExtensionStateAnimation extends TransparentOverlayAnimationRunnable {
        public BottomBarExtensionStateAnimation(BottomBarExtensionState targetState) {
            this(targetState, ANIMATION_DURATION_BOTTOM_BAR, true);
        }

        BottomBarExtensionStateAnimation(BottomBarExtensionState targetState, long duration,
                                         boolean updateExtensionStateField) {
            super(duration,
                  mBottomBarTabPager.getMeasuredHeight(),
                  getBottomBarTabPagerHeightForState(targetState));
            if (updateExtensionStateField) {
                mBottomBarExtensionState = targetState;
            }
        }

        @Override
        protected void onAnimationUpdate(int value) {
            setViewHeight(mBottomBarTabPager, value);
        }
    }

    public abstract class TransparentOverlayAnimationRunnable implements Runnable {
        private final long mDuration;
        private final int mInitialOverlayHeight;
        private final int mTargetOverlayHeight;

        protected final int mInitialViewHeight;

        public TransparentOverlayAnimationRunnable(long duration, int currentViewHeight, int targetViewHeight) {
            mDuration = duration;
            mInitialViewHeight = currentViewHeight;
            mInitialOverlayHeight = mTransparentOverlay.getMeasuredHeight();
            mTargetOverlayHeight = mInitialOverlayHeight - (targetViewHeight - currentViewHeight);
        }

        protected abstract void onAnimationUpdate(int value);

        @Override
        public void run() {
            ValueAnimator va = ValueAnimator.ofInt(mInitialOverlayHeight, mTargetOverlayHeight);
            va.setDuration(mDuration);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int height = (Integer) animation.getAnimatedValue();
                    setViewHeight(mTransparentOverlay, height);
                    TransparentOverlayAnimationRunnable.this.onAnimationUpdate((mInitialOverlayHeight - height) + mInitialViewHeight);
                }
            });
            va.start();
        }
    }

    public enum BottomBarExtensionState {
        None(0),
        HalfSize(1),
        Max(2);

        private final int mValue;

        BottomBarExtensionState(int value) {
            mValue = value;
        }

        public static BottomBarExtensionState fromValue(int value) {
            for (BottomBarExtensionState type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return None;
        }

        public int toValue() {
            return mValue;
        }
    }
}
