package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import derpibooru.derpy.R;
import derpibooru.derpy.data.types.DerpibooruImageInfo;

public class ImageInfoTabFragment extends Fragment {
    public ImageInfoTabFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_info_tab, container, false);

        DerpibooruImageInfo info = this.getArguments().getParcelable("image_info");

        ((TextView) v.findViewById(R.id.textUploaded))
                .setText(info.getUploader() + " " + info.getDescription() + " " + info.getSourceUrl());

        return v;
    }
}
