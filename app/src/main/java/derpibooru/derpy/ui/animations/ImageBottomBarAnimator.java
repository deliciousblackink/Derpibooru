package derpibooru.derpy.ui.animations;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Performs bottom bar extension/compression animation.
 */
public class ImageBottomBarAnimator {
    private static final long ANIMATION_DURATION = 200;

    private final int mDefaultExtendedHeight;
    private int mMaximumHeight;
    private int mCompressedHeight;
    private View mBottomBar;

    public ImageBottomBarAnimator(View bottomBar, int maximumHeight) {
        mDefaultExtendedHeight = maximumHeight / 2;
        mMaximumHeight = maximumHeight;
        mCompressedHeight = bottomBar.getMeasuredHeight();
        mBottomBar = bottomBar;
    }

    public void animateBottomBarExtension() {
        animateHeightChange(mCompressedHeight, mDefaultExtendedHeight);
    }

    public void animateBottomBarCompression() {
        animateHeightChange(mDefaultExtendedHeight, mCompressedHeight);
    }

    private void animateHeightChange(int startHeight, int targetHeight) {
        ValueAnimator va = ValueAnimator.ofInt(startHeight, targetHeight);
        va.setDuration(ANIMATION_DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mBottomBar.getLayoutParams().height =
                        (Integer) animation.getAnimatedValue();
                mBottomBar.requestLayout();
            }
        });
        va.start();
    }

    private class LayoutWeightAnimation extends Animation {
        private float mStartWeight;
        private float mDeltaWeight;

        private View mContent;

        public LayoutWeightAnimation(View target) {
            mContent = target;
        }

        public LayoutWeightAnimation from(float weight) {
            mStartWeight = weight;
            return this;
        }

        public LayoutWeightAnimation to(float weight) {
            mDeltaWeight = weight - mStartWeight;
            return this;
        }

        public LayoutWeightAnimation during(long duration) {
            setDuration(duration);
            return this;
        }

        public void run() {
            mContent.startAnimation(this);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
            lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}