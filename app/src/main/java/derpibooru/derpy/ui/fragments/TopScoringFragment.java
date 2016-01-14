package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;
import derpibooru.derpy.server.ImageList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopScoringFragment extends ImageListFragment {
    public TopScoringFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageList il = new ImageList(getContext(), this);
        il.type(ImageList.Type.TopScoring).inDays(3).load();
        /* ImageList handling is implemented in the ImageListFragment abstract class */

        return inflater.inflate(R.layout.layout_gallery_top_scoring, container, false);
    }
}
