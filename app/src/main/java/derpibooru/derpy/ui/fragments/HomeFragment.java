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
        initializeTabViewPager();
        return v;
    }

    @Override
    protected void onUserRefreshed(DerpibooruUser user) {
        if (mTabViewPager != null) {
            ((HomeTabAdapter) mTabViewPager.getFragmentAdapter()).refreshUser(user);
        }
    }

    private void initializeTabViewPager() {
        mTabViewPager.setFragmentAdapter(new HomeTabAdapter(
                getChildFragmentManager() /* http://stackoverflow.com/a/15386994/1726690 */,
                new HomeTabAdapter.TabSetChangeHandler() {
            @Override
            public void onTabSetChanged() {
                mTabViewPager.refreshTabTitles();
            }
        }, getUser()));
    }
}
