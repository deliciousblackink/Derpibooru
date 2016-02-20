package derpibooru.derpy.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;

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

    public void setInfo(int upvotes, int downvotes, int score,
                        List<DerpibooruImageInteraction.InteractionType> interactions) {
        init();
        AccentColorIconButton u = (AccentColorIconButton) this.findViewById(R.id.buttonUpvote);
        u.setText(Integer.toString(upvotes));
        u.setActive(interactions.contains(DerpibooruImageInteraction.InteractionType.Upvote));
        /* prevent icon from blending with the background by disabling tint toggle on touch
         * (in case there was no user interaction) */
        u.setToggleIconTintOnTouch(interactions.contains(DerpibooruImageInteraction.InteractionType.Upvote));

        AccentColorIconButton d = (AccentColorIconButton) this.findViewById(R.id.buttonDownvote);
        d.setText(Integer.toString(downvotes));
        d.setActive(interactions.contains(DerpibooruImageInteraction.InteractionType.Downvote));
        /* prevent icon from blending with the background by disabling tint toggle on touch
         * (in case there was no user interaction) */
        d.setToggleIconTintOnTouch(interactions.contains(DerpibooruImageInteraction.InteractionType.Downvote));

        TextView s = (TextView) this.findViewById(R.id.textScore);
        s.setText(Integer.toString(score));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_top_bar, null);
        addView(view);
    }
}