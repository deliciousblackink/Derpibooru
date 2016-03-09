package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import derpibooru.derpy.server.providers.UserImageListProvider;

public class UserImageListFragment extends ImageListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        super.initializeList(
                new UserImageListProvider(getActivity(), super.getNewInstanceOfProviderQueryHandler())
                        .type(UserImageListProvider.UserListType.fromValue(getArguments().getInt("type"))));
        return v;
    }
}
