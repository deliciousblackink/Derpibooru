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

    private OnClickListener mButtonListener;
    private int mActiveColorResId;

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

    public void setActive(boolean active) {
        if (active) {
            getIcon().setColorFilter(mActiveColorResId, PorterDuff.Mode.SRC_IN);
        } else {
            getIcon().clearColorFilter();
        }
    }

    private Drawable getIcon() {
        return super.getCompoundDrawables()[0];
    }

    private void setIcon(Drawable icon) {
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        setCompoundDrawables(icon, null, null, null);
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
            return onTouchEvent(event);
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
