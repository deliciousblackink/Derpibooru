package derpibooru.derpy.ui.animators;

import android.animation.ValueAnimator;
import android.view.View;

public class ImageBottomBarAnimator {
    private static final long ANIMATION_DURATION_BASE = 200L;

    private final int mMaximumExtension;
    private final int mHalfExtension;
    private final int mHeaderHeight;

    private final View mTransparentOverlay;
    private final View mTabPager;
    private final View mTabPagerHeader;

    private Runnable mRunAfterAnimation;

    public ImageBottomBarAnimator(View transparentOverlay, View tabPager, View tabPagerHeader,
                                  int maximumHeightOfBottomBar) {
        mTransparentOverlay = transparentOverlay;
        mTabPager = tabPager;
        mTabPagerHeader = tabPagerHeader;

        mHeaderHeight = tabPagerHeader.getMeasuredHeight();
        mMaximumExtension = maximumHeightOfBottomBar - mHeaderHeight;
        mHalfExtension = mMaximumExtension / 2;

        setTransparentOverlayInitialHeight(maximumHeightOfBottomBar);
    }

    public ImageBottomBarAnimator doAfter(Runnable runnable) {
        mRunAfterAnimation = runnable;
        return this;
    }

    public ExtensionState getCurrentExtensionState() {
        if (mTabPager.getVisibility() == View.INVISIBLE) {
            return ExtensionState.None;
        }
        if (mTransparentOverlay.getMeasuredHeight() == 0) {
            return ExtensionState.Max;
        }
        return ExtensionState.HalfSize;
    }

    public void extendViewPagerHeader() {
        extendViewPagerHeader(0.6);
    }

    public void extendViewPagerHeader(final double multiplyDurationBy) {
        /* simple "post(Runnable)" is executed before transparent overlay's layout gets measured */
        mTransparentOverlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                new TransparentOverlayHeightAnimator()
                        .to(mHeaderHeight)
                        .multiplyDurationBy(multiplyDurationBy)
                        .doOnFinish(new Runnable() {
                            @Override
                            public void run() {
                                if (mRunAfterAnimation != null) {
                                    mRunAfterAnimation.run();
                                    mRunAfterAnimation = null;
                                }
                            }
                        })
                        .animate(mTabPagerHeader,
                                 (getCurrentOverlayHeight() - mHeaderHeight), (mMaximumExtension + mHeaderHeight));
            }
        }, ANIMATION_DURATION_BASE);
    }

    public void extendViewPager(ExtensionState target) {
        if (target != ExtensionState.None) {
            if (mTabPager.getVisibility() == View.INVISIBLE) {
                mTabPager.setVisibility(View.VISIBLE);
            }
            new TransparentOverlayHeightAnimator()
                    .to((target == ExtensionState.Max) ? mMaximumExtension
                                                                : mHalfExtension)
                    .doOnFinish(new Runnable() {
                        @Override
                        public void run() {
                            if (mRunAfterAnimation != null) {
                                mRunAfterAnimation.run();
                                mRunAfterAnimation = null;
                            }
                        }
                    })
                    .animate(mTabPager);
        }
    }
    
    public void collapseViewPager() {
        new TransparentOverlayHeightAnimator()
                .to(0)
            /* going from fully-extended length to 0 equals twice the usual half-extension */
                .multiplyDurationBy(2)
                .doOnFinish(new Runnable() {
                    @Override
                    public void run() {
                        mTabPager.setVisibility(View.INVISIBLE);
                        if (mRunAfterAnimation != null) {
                            mRunAfterAnimation.run();
                            mRunAfterAnimation = null;
                        }
                    }
                })
                .animate(mTabPager);
    }

    private void setTransparentOverlayInitialHeight(int maximumHeight) {
        mTransparentOverlay.getLayoutParams().height = maximumHeight;
        mTransparentOverlay.requestLayout();
        mTabPagerHeader.getLayoutParams().height = 0;
        mTabPagerHeader.requestLayout();
    }

    private int getCurrentOverlayHeight() {
        return mTransparentOverlay.getMeasuredHeight();
    }

    public enum ExtensionState {
        HalfSize(0),
        Max(1),
        None(2);

        private int mValue;

        ExtensionState(int value) {
            mValue = value;
        }

        public static ExtensionState fromValue(int value) {
            for (ExtensionState type : values()) {
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

    private class TransparentOverlayHeightAnimator {
        private long mAnimationDuration = ANIMATION_DURATION_BASE;
        private int mTargetHeight;
        private Runnable mPostAnimationRunnable;

        TransparentOverlayHeightAnimator to(int height) {
            mTargetHeight = height;
            return this;
        }

        TransparentOverlayHeightAnimator doOnFinish(Runnable runnable) {
            mPostAnimationRunnable = runnable;
            return this;
        }

        TransparentOverlayHeightAnimator multiplyDurationBy(double m) {
            mAnimationDuration *= m;
            return this;
        }

        int calculateTargetOverlayHeight() {
            return mMaximumExtension - mTargetHeight;
        }

        void animate(View target) {
            animate(target, calculateTargetOverlayHeight(), mMaximumExtension);
        }

        void animate(final View target, int targetOverlayHeight, final int maximumExtension) {
            ValueAnimator va = ValueAnimator.ofInt(getCurrentOverlayHeight(), targetOverlayHeight);
            va.setDuration(mAnimationDuration);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int overlayHeight = (Integer) animation.getAnimatedValue();
                    mTransparentOverlay.getLayoutParams().height = overlayHeight;
                    mTransparentOverlay.requestLayout();
                    target.getLayoutParams().height = maximumExtension - overlayHeight;
                    target.requestLayout();
                }
            });
            va.start();
            if (mPostAnimationRunnable != null) {
                mTransparentOverlay.postDelayed(mPostAnimationRunnable, mAnimationDuration);
            }
        }
    }
}
