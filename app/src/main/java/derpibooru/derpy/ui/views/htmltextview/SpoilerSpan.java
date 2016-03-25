package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import derpibooru.derpy.R;

class SpoilerSpan extends ClickableSpan {
    final int pressedColor;
    final int normalColor;

    private boolean mIsPressed;

    SpoilerSpan(Context context) {
        pressedColor = ContextCompat.getColor(context, R.color.colorAccent);
        normalColor = ContextCompat.getColor(context, R.color.colorTextDark);
    }

    @Override
    public void onClick(View widget) { }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mIsPressed ? pressedColor : normalColor);
        ds.bgColor = mIsPressed ? normalColor : pressedColor;
        ds.setUnderlineText(false);
    }
}

