package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;

public class ImageInfoTabFragment extends Fragment {
    public ImageInfoTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ImageList il = new ImageList(getContext(), this);
        //il.type(ImageList.Type.MostCommented).inDays(3).load();
        /* Query handling is implemented in the ImageListFragment abstract class */

        return inflater.inflate(R.layout.fragment_image_info_tab, container, false);
    }
}
