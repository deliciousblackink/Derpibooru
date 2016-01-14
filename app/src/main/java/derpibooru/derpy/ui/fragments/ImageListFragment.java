package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.ImageThumb;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.GalleryAdapter;

public abstract class ImageListFragment extends Fragment
        implements QueryHandler {
    public ImageListFragment() {
    }

    public void queryFailed() {

    }

    public void queryPerformed(Object imageList) {
        ArrayList<ImageThumb> images = (ArrayList<ImageThumb>) imageList;

        GridView gv = (GridView) getView().findViewById(R.id.gallery);
        GalleryAdapter ga = new GalleryAdapter(getContext(), R.layout.view_gallery_item, images);
        gv.setAdapter(ga);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                int imgId = (int)
                        ((GalleryAdapter.ViewHolder) v.getTag()).info.getTag();
                intent.putExtra("id", imgId);
                startActivity(intent);
            }
        });
    }
}
