package derpibooru.derpy.ui.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;

public class ImageBottomBarFavoritesTabFragment extends ImageBottomBarTabFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_image_bottom_bar_favorites, container, false);
        if (getArguments().containsKey("info")) {
            displayInfoInView(v, (DerpibooruImageDetailed) getArguments().getParcelable("info"));
        }
        return v;
    }

    @Override
    protected void displayInfoInView(View target, DerpibooruImageDetailed info) {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.view_image_bottom_bar_favorites_item,
                                                     info.getFavedBy());
        ((GridView) target.findViewById(R.id.gridFavedBy)).setAdapter(aa);
        target.findViewById(R.id.progressBottomBarTab).setVisibility(View.GONE);
        target.findViewById(R.id.textFavedBy).setVisibility(View.VISIBLE);
        target.findViewById(R.id.gridFavedBy).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLinkClick(View view) { }
}
