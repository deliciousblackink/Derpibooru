package derpibooru.derpy.ui.views.htmltextview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

public class ImageActionDialogFragment extends DialogFragment {
    public static final String EXTRAS_IMAGE_ACTION_REPRESENTATION = "derpibooru.derpy.ImageActionStringRepresentation";

    @Bind(R.id.imageView) ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_image_fragment_embedded_image_dialog, container, false);
        ButterKnife.bind(this, v);
        loadImage();
        return v;
    }

    private void loadImage() {
        int maxWidth = getResources().getDisplayMetrics().widthPixels;
        int maxHeight = getResources().getDisplayMetrics().heightPixels;
        GlideViewTarget target = new GlideViewTarget(imageView, maxWidth, maxHeight);
        ImageAction action = ImageAction.fromStringRepresentation(
                getArguments().getString(EXTRAS_IMAGE_ACTION_REPRESENTATION));
        Glide.with(this)
                .load(action.getImageSource())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(target);
    }

    @OnClick(R.id.buttonDismiss)
    void dismissDialog() {
        dismiss();
    }

    private class GlideViewTarget extends ViewTarget<ImageView, GlideDrawable> {
        private final View mTarget;
        private final int mMaxTargetWidth;
        private final int mMaxTargetHeight;

        private GlideViewTarget(ImageView target, int maxTargetWidth, int maxTargetHeight) {
            super(target);
            mTarget = target;
            mMaxTargetWidth = maxTargetWidth;
            mMaxTargetHeight = maxTargetHeight;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            AdaptiveBoundsGlideResourceDrawable drawable = new AdaptiveBoundsGlideResourceDrawable();
            drawable.setResource(resource, mTarget, mMaxTargetWidth, mMaxTargetHeight);
            if (drawable.isAnimated()) {
                drawable.playAnimated(true);
            }
        }
    }
}
