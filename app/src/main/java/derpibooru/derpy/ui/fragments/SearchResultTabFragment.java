package derpibooru.derpy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.SearchResultProvider;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.adapters.ImageListAdapter;

public class SearchResultTabFragment extends ImageListTabFragment
                                     implements QueryResultHandler {
    public SearchResultTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SearchResultProvider provider = new SearchResultProvider(getActivity(), this);
        provider.search(getArguments().getString("query"));

        /* note: since search results are presented in an image grid, they are
         * basically an extension of a list view; hence the image list tab layout
         * can be reused without unnecessary copy-paste.
         */
        return inflater.inflate(R.layout.fragment_image_list_tab, container, false);
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
