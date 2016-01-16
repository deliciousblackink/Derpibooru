package derpibooru.derpy.ui.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Performs bottom bar extension/compression animation by
 * changing the layout weight of the container located above
 * the bar.
 *
 * This approach prevents the bar from overlapping other layout
 * elements. It does not work inside scrollable views as it
 * assumes the full layout of the bar to be hidden beneath the screen.
 */
public class ImageBottomBarAnimator {
    private static final long ANIMATION_DURATION = 200;
    private static final float IMAGE_VIEW_EXTENDED_LAYOUT_WEIGHT = 4.0f;
    private static final float IMAGE_VIEW_COMPRESSED_LAYOUT_WEIGHT = 2.0f;

    private View mTopView;

    /**
     * @param topView the View located above the bar
     */
    public ImageBottomBarAnimator(View topView) {
        mTopView = topView;
    }

    /**
     * Frees layout space for the bar by compressing
     * the View located above it.
     */
    public void animateBottomBarExtension() {
        new LayoutWeightAnimation(mTopView)
                .from(IMAGE_VIEW_EXTENDED_LAYOUT_WEIGHT)
                .to(IMAGE_VIEW_COMPRESSED_LAYOUT_WEIGHT)
                .during(ANIMATION_DURATION)
                .run();
    }

    /**
     * Hides the bar by extending the View above it.
     */
    public void animateBottomBarCompression() {
        new LayoutWeightAnimation(mTopView)
                .from(IMAGE_VIEW_COMPRESSED_LAYOUT_WEIGHT)
                .to(IMAGE_VIEW_EXTENDED_LAYOUT_WEIGHT)
                .during(ANIMATION_DURATION)
                .run();
    }

    private class LayoutWeightAnimation extends Animation {
        /* http://stackoverflow.com/questions/18024591/change-the-weight-of-layout-with-an-animation */
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