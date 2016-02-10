package derpibooru.derpy.ui.animations;

import android.animation.ValueAnimator;
import android.view.View;

public class ImageListItemAnimator {
    private static final int ANIMATION_DURATION = 250;

    public void clearView(View v) {
        animateViewHeight(v, 0, 0);
    }

    public void collapseView(View v) {
        animateViewHeight(v, v.getMeasuredHeight(), 0);
    }

    public void expandView(View v, int targetHeight) {
        animateViewHeight(v, 0, targetHeight);
    }

    private void animateViewHeight(final View v, int from, int to) {
        ValueAnimator va = ValueAnimator.ofInt(from, to);
        va.setDuration(ANIMATION_DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                v.requestLayout();
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        });
        va.start();
    }
}
