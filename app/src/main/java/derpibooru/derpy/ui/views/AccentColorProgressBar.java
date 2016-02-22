package derpibooru.derpy.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import derpibooru.derpy.R;

public class AccentColorProgressBar extends ProgressBar {
    public AccentColorProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleLarge);
        init(context);
    }

    public AccentColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.progressBarStyleLarge);
        init(context);
    }

    public AccentColorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(context, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
    }
}
