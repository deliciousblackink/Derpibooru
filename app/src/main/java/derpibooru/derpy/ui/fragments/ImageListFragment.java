package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.ImageListProvider;
import derpibooru.derpy.server.ProviderRequestHandler;
import derpibooru.derpy.ui.adapters.ImageListAdapter;
import derpibooru.derpy.ui.views.RecyclerViewEndlessScrollListener;
import derpibooru.derpy.ui.views.ImageListRecyclerView;

public abstract class ImageListFragment extends Fragment {
    private ImageListAdapter mImageListAdapter;
    private ImageListRecyclerView mImageView;
    private ImageListProvider mImageListProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_list_tab, container, false);
        mImageView = (ImageListRecyclerView) v.findViewById(R.id.viewImages);
        if (mImageListProvider != null) {
            fetchImageThumbs();
        } else {
            Log.e("ImageListFragment", "call setImageListProvider before super.onCreateView");
        }
        return v;
    }

    protected void setImageListProvider(ImageListProvider provider) {
        mImageListProvider = provider;
    }

    protected ImageListProvider getImageListProvider() {
        return mImageListProvider;
    }

    protected class ImageListRequestHandler implements ProviderRequestHandler {
        @Override
        public void onRequestCompleted(Object result) {
            displayImagesFromProvider((ArrayList<DerpibooruImageThumb>) result);
        }

        @Override
        public void onRequestFailed() {
            /* TODO: display error message */
        }
    }

    protected abstract void fetchImageThumbs();

    protected void resetImageListAdapter() {
        mImageListAdapter = null;
    }

    private void displayImagesFromProvider(ArrayList<DerpibooruImageThumb> imageThumbs) {
        if (mImageListAdapter == null) {
            mImageListAdapter = new ImageListAdapter(getActivity(), imageThumbs);
            mImageView.setAdapter(mImageListAdapter);
            mImageView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                    (GridLayoutManager) mImageView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page) {
                    mImageListProvider.nextPage().fetch();
                }
            });
        } else {
            mImageListAdapter.appendImageThumbs(imageThumbs);
        }
    }
}
