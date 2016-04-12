package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.ImageActivity;

public class ImageBottomBarFavoritesTabFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_favorites, container, false);
        displayInfoInView(v, (DerpibooruImageDetailed)
                getArguments().getParcelable(ImageActivity.EXTRAS_IMAGE_DETAILED));
        return v;
    }

    protected void displayInfoInView(View target, DerpibooruImageDetailed info) {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.view_image_detailed_bottom_bar_favorites_item,
                                                     info.getFavedBy());
        ((GridView) target.findViewById(R.id.gridFavedBy)).setAdapter(aa);
        target.findViewById(R.id.textFavedBy).setVisibility(View.VISIBLE);
        target.findViewById(R.id.gridFavedBy).setVisibility(View.VISIBLE);
    }
}
