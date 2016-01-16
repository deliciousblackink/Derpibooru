package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageInfo;

public class ImageFavoritesTabFragment extends Fragment {
    public ImageFavoritesTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_favorites_tab, container, false);

        DerpibooruImageInfo info = this.getArguments().getParcelable("image_info");

        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getActivity(), R.layout.view_image_favorites_item,
                                   info.getFavedBy());
        ((ListView) v.findViewById(R.id.listFavedBy))
                .setAdapter(aa);

        return v;
    }
}
