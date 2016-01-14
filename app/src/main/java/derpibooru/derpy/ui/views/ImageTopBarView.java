package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import derpibooru.derpy.R;

public class ImageTopBarView extends FrameLayout {
    public ImageTopBarView(Context context) {
        super(context);
    }

    public ImageTopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setInfo(int upvotes, int downvotes, int score) {
        init();
        TextView u = (TextView) this.findViewById(R.id.textUpvotes);
        u.setText(Integer.toString(upvotes));
        TextView d = (TextView) this.findViewById(R.id.textDownvotes);
        d.setText(Integer.toString(downvotes));
        TextView s = (TextView) this.findViewById(R.id.textScore);
        s.setText(Integer.toString(score));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_top_bar, null);
        addView(view);
    }
}