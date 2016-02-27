package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.ui.adapters.HomeTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

public class HomeFragment extends UserFragment {
    @Bind(R.id.fragmentPagerView) FragmentTabPagerView mTabViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, v);
        initializeTabViewPager(getUser());
        return v;
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser newUser) {
        if (mTabViewPager != null) {
            DerpibooruUser oldUserData = ((HomeTabAdapter) mTabViewPager.getFragmentAdapter()).getUser();
            if ((oldUserData.isLoggedIn() != newUser.isLoggedIn())
                    || (!oldUserData.getCurrentFilter().equals(newUser.getCurrentFilter()))) {
                /* due to the way ImageListFragment restores its state, it's easier to recreate all tabs at once than
                 * to make each separate one refresh itself (which, in turn, would require hacky workarounds to be placed
                 * inside the ImageListFragment) */
                initializeTabViewPager(newUser);
            }
        }
    }

    private void initializeTabViewPager(DerpibooruUser user) {
        mTabViewPager.setFragmentAdapter(new HomeTabAdapter(getChildFragmentManager(), user));
    }
}
