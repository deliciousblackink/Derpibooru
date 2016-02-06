package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;

public class ImageBottomBarFavoritesTabFragment extends ImageBottomBarTabFragment {
    public ImageBottomBarFavoritesTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_favorites_tab, container, false);
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageInfo) getArguments().getParcelable("info"));
        }
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageInfo info) {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.view_image_favorites_item,
                                                     info.getFavedBy());
        ((GridView) target.findViewById(R.id.gridFavedBy)).setAdapter(aa);
        target.findViewById(R.id.progressBottomBarTab).setVisibility(View.GONE);
        target.findViewById(R.id.textFavedBy).setVisibility(View.VISIBLE);
        target.findViewById(R.id.gridFavedBy).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLinkClick(View view) {

    }
}
