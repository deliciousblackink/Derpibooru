package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

public class ImageBottomBarFavoritesTabFragment extends ImageBottomBarTabFragment {
    private static final ImageBottomBarTabAdapter.ImageBottomBarTab TAB_ID =
        ImageBottomBarTabAdapter.ImageBottomBarTab.Faves;

    public ImageBottomBarFavoritesTabFragment() {
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
        ((GridView) v.findViewById(R.id.gridFavedBy))
                .setAdapter(aa);

        return v;
    }

    @Override
    protected void onLinkClick(View view) {

    }
}
