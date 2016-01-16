package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageThumb;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;

public abstract class ImageListFragment extends Fragment
        implements QueryHandler {
    public ImageListFragment() {
    }

    public void queryFailed() {

    }

    public void queryPerformed(Object imageList) {
        ArrayList<DerpibooruImageThumb> images = (ArrayList<DerpibooruImageThumb>) imageList;

        GridView gv = (GridView) getView().findViewById(R.id.gallery);
        ImageListAdapter ila = new ImageListAdapter(getContext(), R.layout.view_gallery_item, images);
        gv.setAdapter(ila);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra("image_data", ((ImageListAdapter.ViewHolder) v.getTag()).data);
                startActivity(intent);
            }
        });
    }
}
