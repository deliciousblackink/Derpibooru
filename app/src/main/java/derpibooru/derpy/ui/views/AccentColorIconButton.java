package derpibooru.derpy.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import derpibooru.derpy.R;

public class AccentColorIconButton extends TextView {
    private static final int ICON_PADDING_IN_DIP = 5;
    private int mActiveColorDarkResId;
    private int mActiveColorResId;

    private OnClickListener mButtonListener;
    private Integer mCurrentButtonColor;

    public AccentColorIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AccentColorIconButton);
        try {
            setButtonText(a.getString(R.styleable.AccentColorIconButton_buttonText));
            setIcon(a.getDrawable(R.styleable.AccentColorIconButton_buttonIcon));
        } finally {
            a.recycle();
        }

        super.setOnTouchListener(new ButtonTouchListener());
        super.setOnClickListener(new ButtonClickListener());

        mActiveColorDarkResId = ContextCompat.getColor(context, R.color.colorAccentDark);
        mActiveColorResId = ContextCompat.getColor(context, R.color.colorAccent);
    }

    public void setButtonText(CharSequence text) {
        super.setText(text);
        if (text.length() > 0) {
            super.setGravity(Gravity.CENTER);
            super.setCompoundDrawablePadding((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ICON_PADDING_IN_DIP, getResources().getDisplayMetrics()));
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mButtonListener = listener;
    }

    public void setActive(boolean makeActive) {
        if (makeActive) {
            if (mCurrentButtonColor == null) {
                mCurrentButtonColor = mActiveColorResId;
                getIcon().setColorFilter(mCurrentButtonColor, PorterDuff.Mode.SRC_IN);
            } else {
                mCurrentButtonColor = mActiveColorDarkResId;
                getIcon().setColorFilter(mActiveColorDarkResId, PorterDuff.Mode.SRC_IN);
            }
        } else {
            if (mCurrentButtonColor == null || mCurrentButtonColor == mActiveColorResId) {
                mCurrentButtonColor = null;
                getIcon().clearColorFilter();
            } else {
                mCurrentButtonColor = mActiveColorResId;
                getIcon().setColorFilter(mCurrentButtonColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (isEnabled() && super.onTouchEvent(event));
    }

    private Drawable getIcon() {
        return super.getCompoundDrawables()[0];
    }

    private void setIcon(Drawable icon) {
        Drawable mutableIcon = icon.mutate();
        mutableIcon.setBounds(0, 0, mutableIcon.getIntrinsicWidth(), mutableIcon.getIntrinsicHeight());
        setCompoundDrawables(mutableIcon, null, null, null);
    }

    private class ButtonTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                setActive(true);
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                setActive(false);
            }
            return false;
        }
    }

    private class ButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mButtonListener != null) {
                mButtonListener.onClick(v);
            }
        }
    }
}
