package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageListType;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.ImageListProvider;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;

public class ImageListTabFragment extends Fragment
                                  implements QueryResultHandler {
    public ImageListTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadImages();
        return inflater.inflate(R.layout.fragment_image_list_tab, container, false);
    }

    protected void loadImages() {
        ImageListProvider provider = new ImageListProvider(getActivity(), this);
        provider
                .type(DerpibooruImageListType.getFromValue(getArguments().getInt("type")))
                .inDays(3) /* TODO: pass the time limit as an argument */
                .load();
    }

    public void onQueryFailed() {

    }

    public void onQueryExecuted(Object imageList) {
        ArrayList<DerpibooruImageThumb> images = (ArrayList<DerpibooruImageThumb>) imageList;

        /* TODO: NullPointerException when changing phone's orientation */
        GridView gv = (GridView) getView().findViewById(R.id.imageGrid);
        ImageListAdapter ila = new ImageListAdapter(getActivity(), R.layout.view_image_list_item, images);
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
