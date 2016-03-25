package derpibooru.derpy.ui.views.htmltextview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import derpibooru.derpy.R;

class SpoilerSpan extends ClickableSpan {
    final int spoileredColor;
    final int unspoileredColor;
    final int unspoileredBackgroundColor;

    private boolean mUnspoilered;

    SpoilerSpan(Context context) {
        spoileredColor = ContextCompat.getColor(context, R.color.colorAccentLight);
        unspoileredColor = ContextCompat.getColor(context, R.color.colorTextGray);
        unspoileredBackgroundColor = ContextCompat.getColor(context, android.R.color.white);
    }

    @Override
    public void onClick(View widget) { }

    public void unspoiler() {
        mUnspoilered = true;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mUnspoilered ? unspoileredColor : spoileredColor);
        ds.bgColor = mUnspoilered ? unspoileredBackgroundColor : spoileredColor;
        ds.setUnderlineText(false);
    }
}

