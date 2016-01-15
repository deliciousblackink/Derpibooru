package derpibooru.derpy.ui.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/* http://stackoverflow.com/questions/18024591/change-the-weight-of-layout-with-an-animation */
public class ExpandViewAnimation extends Animation {
    private final float mStartWeight;
    private final float mDeltaWeight;

    private View mContent;

    public ExpandViewAnimation(View content,
                               float startWeight, float endWeight) {
        mContent = content;

        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
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