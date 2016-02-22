package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import derpibooru.derpy.R;

public class ImageTopBarView extends FrameLayout {
    public ImageTopBarView(Context context) {
        super(context);
        init();
    }

    public ImageTopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageTopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AccentColorIconButton getScoreButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonScore);
    }

    public AccentColorIconButton getUpvoteButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonUpvote);
    }

    public AccentColorIconButton getDownvoteButton() {
        return (AccentColorIconButton) findViewById(R.id.buttonDownvote);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_top_bar, null);
        addView(view);
        getScoreButton().setEnabled(false); /* the score button is not a touchable view */
    }
}