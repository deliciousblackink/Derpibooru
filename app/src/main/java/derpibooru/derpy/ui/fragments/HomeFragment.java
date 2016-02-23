package derpibooru.derpy.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.ui.adapters.HomeTabAdapter;
import derpibooru.derpy.ui.views.FragmentTabPagerView;

public class HomeFragment extends Fragment {
    @Bind(R.id.fragmentPagerView) private FragmentTabPagerView mTabViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(v);
        initializeTabViewPager();
        return v;
    }

    private void initializeTabViewPager() {
        mTabViewPager.setFragmentAdapter(
                new HomeTabAdapter(getContext(), getFragmentManager(),
                                   new HomeTabAdapter.TabSetChangeHandler() {
                                       @Override
                                       public void onTabSetChanged() {
                                           mTabViewPager.refreshTabTitles();
                                       }
                                   }));
    }

    /*@Override
    public void onUserDataRefreshed() {
        if (mTabViewPager != null) {
            ((HomeTabAdapter) mTabViewPager.getFragmentAdapter()).toggleWatchedTab();
        }
    } */
}
