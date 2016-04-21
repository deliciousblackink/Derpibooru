package derpibooru.derpy.ui.views.imagedetailedview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public class ImageTopBarView extends FrameLayout {
    @Bind(R.id.buttonScore) AccentColorIconButton buttonScore;
    @Bind(R.id.buttonUpvote) AccentColorIconButton buttonUpvote;
    @Bind(R.id.buttonDownvote) AccentColorIconButton buttonDownvote;

    public ImageTopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(getContext(), R.layout.view_image_detailed_top_bar, null);
        ButterKnife.bind(this, view);
        addView(view);
    }
}