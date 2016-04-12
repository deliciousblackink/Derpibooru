package derpibooru.derpy.ui.fragments.imageactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.providers.TagProvider;

public class ImageActivityTagFragment extends Fragment {
    public static final String EXTRAS_TAG_ID = "derpibooru.derpy.TagId";
    private static final String EXTRAS_FETCHED_TAG = "derpibooru.derpy.FetchedTag";

    @Bind(R.id.textTag) TextView textTagName;
    @Bind(R.id.viewTagImage) ImageView viewTagImage;
    @Bind(R.id.textTagShortDescription) TextView textTagShortDescription;
    @Bind(R.id.textTagDescription) TextView textTagDescription;

    @Bind(R.id.layoutRoot) View rootView;
    @Bind(R.id.progressTag) View progressBar;
    @Bind(R.id.layoutUserActions) View userActions;

    private ImageActivityTagFragmentHandler mActivityCallbacks;
    private DerpibooruTagDetailed mFetchedTag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_image_fragment_tag, container, false);
        ButterKnife.bind(this, v);
        if ((savedInstanceState != null)
                && (savedInstanceState.getParcelable(EXTRAS_FETCHED_TAG) != null)) {
            mFetchedTag = savedInstanceState.getParcelable(EXTRAS_FETCHED_TAG);
            displayFetchedTag();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            fetchTagInformation();
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRAS_FETCHED_TAG, mFetchedTag);
    }

    public void setActivityCallbacks(ImageActivityTagFragmentHandler handler) {
        mActivityCallbacks = handler;
    }

    private void fetchTagInformation() {
        new TagProvider(getContext(), new TagProviderRequestHandler())
                .tags(Collections.singletonList(getArguments().getInt(EXTRAS_TAG_ID)))
                .overrideCache()
                .fetch();
    }

    private void displayFetchedTag() {
        rootView.setVisibility(View.VISIBLE);
        String tag = String.format("%s (%d)", mFetchedTag.getName(), mFetchedTag.getNumberOfImages());
        textTagName.setText(tag);
        mActivityCallbacks.setToolbarTitle(tag);
        if (!mFetchedTag.getShortDescription().isEmpty()) {
            textTagShortDescription.setText(mFetchedTag.getShortDescription());
        } else {
            textTagShortDescription.setVisibility(View.GONE);
        }
        if (!mFetchedTag.getDescription().isEmpty()) {
            textTagDescription.setText(mFetchedTag.getDescription());
        } else {
            textTagDescription.setVisibility(View.GONE);
        }
        if (!mFetchedTag.getSpoilerUrl().isEmpty()) {
            Glide.with(getContext()).load(mFetchedTag.getSpoilerUrl())
                    .centerCrop()
                    .crossFade()
                    .into(viewTagImage);
        } else {
            viewTagImage.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.buttonSearch)
    void openSearchForTag() {
        if (mActivityCallbacks != null) {
            mActivityCallbacks.onTagSearchRequested(mFetchedTag.getName());
        }
    }

    private class TagProviderRequestHandler implements QueryHandler<List<DerpibooruTagDetailed>> {
        @Override
        public void onQueryExecuted(List<DerpibooruTagDetailed> result) {
            mFetchedTag = result.get(0);
            progressBar.setVisibility(View.INVISIBLE);
            displayFetchedTag();
        }

        @Override
        public void onQueryFailed() { }
    }

    public interface ImageActivityTagFragmentHandler {
        void onTagSearchRequested(String tagName);
        void setToolbarTitle(String title);
    }
}
