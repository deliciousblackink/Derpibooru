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
import android.widget.RelativeLayout;
import android.widget.TextView;

import derpibooru.derpy.R;

public class AccentColorIconButton extends RelativeLayout {
    private TextViewButton mTextViewWithIcon;
    private OnClickListener mButtonListener;

    private boolean mToggleIconTintOnTouch = true;

    public AccentColorIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextViewWithIcon = new TextViewButton(context, attrs);
        super.addView(mTextViewWithIcon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        super.setGravity(Gravity.CENTER);
        super.setClickable(true);
        super.setOnTouchListener(new ButtonTouchListener());
        super.setOnClickListener(new ButtonClickListener());
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mButtonListener = listener;
    }

    public void setText(CharSequence text) {
        mTextViewWithIcon.setText(text);
    }

    public void setActive(boolean active) {
        mTextViewWithIcon.setKeepActive(active);
        mTextViewWithIcon.setActive(active);
    }

    public void setToggleIconTintOnTouch(boolean toggle) {
        mToggleIconTintOnTouch = toggle;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mTextViewWithIcon.setEnabled(enabled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (isEnabled() && super.onTouchEvent(event));
    }

    private class ButtonTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mToggleIconTintOnTouch) {
                return false;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                mTextViewWithIcon.setActive(true);
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                mTextViewWithIcon.setActive(false);
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

    private static class TextViewButton extends TextView {
        private static final int ICON_PADDING_IN_DIP = 5;

        private int mActiveColorResId;
        private boolean mKeepColorFilter = false;
        private boolean mColorFilterSet = false;

        public TextViewButton(Context context, AttributeSet attrs) {
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
            mActiveColorResId = ContextCompat.getColor(context, R.color.colorAccent);
        }

        public void setActive(boolean makeActive) {
            if ((makeActive && !mColorFilterSet) || (!makeActive && mKeepColorFilter)) {
                mColorFilterSet = true;
                getIcon().setColorFilter(mActiveColorResId, PorterDuff.Mode.SRC_IN);
            } else {
                mColorFilterSet = false;
                getIcon().clearColorFilter();
            }
        }

        public void setButtonText(CharSequence text) {
            super.setText(text);
            if (text.length() > 0) {
                super.setGravity(Gravity.CENTER);
                super.setCompoundDrawablePadding((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, ICON_PADDING_IN_DIP, getResources().getDisplayMetrics()));
            }
        }

        public void setKeepActive(boolean keepActive) {
            mKeepColorFilter = keepActive;
        }

        private Drawable getIcon() {
            return super.getCompoundDrawables()[0];
        }

        private void setIcon(Drawable icon) {
            Drawable mutableIcon = icon.mutate();
            mutableIcon.setBounds(0, 0, mutableIcon.getIntrinsicWidth(), mutableIcon.getIntrinsicHeight());
            setCompoundDrawables(mutableIcon, null, null, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return (isEnabled() && super.onTouchEvent(event));
        }
    }
}
