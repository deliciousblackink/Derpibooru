package derpibooru.derpy.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import derpibooru.derpy.R;

public class FragmentTabPagerView extends LinearLayout {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public FragmentTabPagerView(Context context) {
        super(context);
        init();
    }

    public FragmentTabPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FragmentTabPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_fragment_tab_pager, null);
        addView(view);

        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    public void setFragmentAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        setTabsTypeface();
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * Sets the TabLayout typeface to Roboto Medium, as per the guidelines
     * of Material Design (versions prior to Lollipop do not have it pre-installed).
     */
    private void setTabsTypeface() {
        //http://stackoverflow.com/a/31067431/1726690
        ViewGroup vg = (ViewGroup) mTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild)
                            .setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                                                                  "fonts/Roboto-Medium.ttf"));
                }
            }
        }
    }
}
