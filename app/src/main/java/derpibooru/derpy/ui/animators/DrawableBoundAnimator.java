package derpibooru.derpy.ui.animators;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class DrawableBoundAnimator {
    private static final long ANIMATION_DURATION = 200L;

    private final Drawable mTarget;
    private final TextView mTargetHolder;

    public DrawableBoundAnimator(Drawable target, TextView targetHolder) {
        mTarget = target;
        mTargetHolder = targetHolder;
    }

    public void animateRightBottom(int targetRight, int targetBottom) {
        ValueAnimator right = ValueAnimator.ofInt(mTarget.getBounds().right, targetRight);
        right.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTarget.setBounds(mTarget.getBounds().left,
                                  mTarget.getBounds().top,
                                  (Integer) animation.getAnimatedValue(),
                                  mTarget.getBounds().bottom);
                mTargetHolder.setText(mTargetHolder.getText());
                mTargetHolder.invalidate();
            }
        });
        ValueAnimator bottom = ValueAnimator.ofInt(mTarget.getBounds().bottom, targetBottom);
        right.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTarget.setBounds(mTarget.getBounds().left,
                                  mTarget.getBounds().top,
                                  mTarget.getBounds().right,
                                  (Integer) animation.getAnimatedValue());
                mTargetHolder.setText(mTargetHolder.getText());
                mTargetHolder.invalidate();
            }
        });
        AnimatorSet rectAnimation = new AnimatorSet();
        rectAnimation.playTogether(right, bottom);
        rectAnimation.setDuration(ANIMATION_DURATION).start();
    }
}
