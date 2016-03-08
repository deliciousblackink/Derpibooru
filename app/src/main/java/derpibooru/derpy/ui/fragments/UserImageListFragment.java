package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.ImageListProvider;
import derpibooru.derpy.server.providers.UserImageListProvider;

public class UserImageListFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.setImageListProvider(
                new UserImageListProvider(getActivity(), new ImageListRequestHandler())
                        .type(UserImageListProvider.UserListType.fromValue(getArguments().getInt("type"))));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ImageListProvider getImageListProviderWithParameters(ImageListProvider target) {
        return target;
    }
}
