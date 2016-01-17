package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.R;
import derpibooru.derpy.server.ImageListProvider;

public class MostCommentedFragment extends ImageListFragment {
    public MostCommentedFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageListProvider ilp = new ImageListProvider(getActivity(), this);
        ilp.type(ImageListProvider.Type.MostCommented).inDays(3).load();
        /* Query handling is implemented in the ImageListFragment abstract class */

        return inflater.inflate(R.layout.fragment_most_commented, container, false);
    }
}
