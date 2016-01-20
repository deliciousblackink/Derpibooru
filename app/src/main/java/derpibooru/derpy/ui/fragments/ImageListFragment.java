package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageThumb;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;

public abstract class ImageListFragment extends Fragment
        implements QueryResultHandler {
    public ImageListFragment() {
    }

    public void onQueryFailed() {

    }

    public void onQueryExecuted(Object imageList) {
        ArrayList<DerpibooruImageThumb> images = (ArrayList<DerpibooruImageThumb>) imageList;

        GridView gv = (GridView) getView().findViewById(R.id.gallery);
        ImageListAdapter ila = new ImageListAdapter(getActivity(), R.layout.view_gallery_item, images);
        gv.setAdapter(ila);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("image_thumb", ((ImageListAdapter.ViewHolder) v.getTag()).data);
                startActivity(intent);
            }
        });
    }
}
