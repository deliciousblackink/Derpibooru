package derpibooru.derpy.ui.views;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

public class ImageBottomBarView extends FrameLayout {
    private static final BiMap<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> TABS =
            ImmutableBiMap.<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab>builder()
            .put(R.id.buttonInfo, ImageBottomBarTabAdapter.ImageBottomBarTab.ImageInfo)
            .put(R.id.buttonFaves, ImageBottomBarTabAdapter.ImageBottomBarTab.Faves)
            .put(R.id.buttonComments, ImageBottomBarTabAdapter.ImageBottomBarTab.Comments).build();

    private ImageBottomBarScrollView mBottomBarScroll;
    private FragmentManager mFragmentManager;
    private ViewPager mPager;
    private int mExtensionHeightOnHeaderButtonClick;
    private boolean mTabsHaveLoaded = false;

    public ImageBottomBarView(Context context) {
        super(context);
    }

    public ImageBottomBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBottomBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageBottomBarView setFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
        return this;
    }

    public ImageBottomBarView setBasicInfo(int faves, int comments) {
        init();
        TextView d = (TextView) this.findViewById(R.id.textFaves);
        d.setText(Integer.toString(faves));
        TextView u = (TextView) this.findViewById(R.id.textComments);
        u.setText(Integer.toString(comments));
        return this;
    }

    public ImageBottomBarView setBarExtensionAttrs(int maximumExtensionHeight) {
        mExtensionHeightOnHeaderButtonClick = maximumExtensionHeight / 2;

        int overlayHeight = (maximumExtensionHeight - getRootView().findViewById(R.id.bottomBarHeaderLayout).getMeasuredHeight());
        getRootView().findViewById(R.id.transparentOverlay).getLayoutParams().height = overlayHeight;
        getRootView().findViewById(R.id.transparentOverlay).requestLayout();

        getRootView().getLayoutParams().height = maximumExtensionHeight;
        getRootView().requestLayout();

        mBottomBarScroll
                .setAnchorViewForStickyHeader(getRootView().findViewById(R.id.bottomBarHeaderAnchor))
                .setStickyHeaderView(getRootView().findViewById(R.id.bottomBarHeaderLayout));
        return this;
    }

    public ImageBottomBarView setOverlayTouchHandler(final TransparentOverlayTouchHandler handler) {
        /* disable scrollview on transparent overlay */
        mBottomBarScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                getRootView().findViewById(R.id.transparentOverlay).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        /* dispatch the transparent overlay touch event to the handler */
        getRootView().findViewById(R.id.transparentOverlay).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                handler.onTouch(event);
                return true;
            }
        });
        return this;
    }

    public ImageBottomBarView setTabInfo(DerpibooruImageInfo info) {
        setUpViewPager(info);
        return this;
    }

    private void toggleButton(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            navigateViewPagerToTheCurrentlySelectedTab();
        } else {
            v.setSelected(false);
            /* play ViewPager collapsing animation and set its visibility to INVISIBLE */
            scrollToPositionAndLimitScrollingPastIt(0);
        }
    }

    private void navigateViewPagerToTheCurrentlySelectedTab() {
        if (getCurrentTab() != null) {
            extendViewPagerIfHidden();
            mPager.setCurrentItem(getCurrentTab().id(), true);
            if (mTabsHaveLoaded) {
                ((ImageBottomBarTabAdapter) mPager.getAdapter())
                        .provideCurrentContentHeight(getCurrentTab());
            }
        }
    }

    private ImageBottomBarTabAdapter.ImageBottomBarTab getCurrentTab() {
        for (Map.Entry<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> tab : TABS.entrySet()) {
            if (findViewById(tab.getKey()).isSelected()) {
                return tab.getValue();
            }
        }
        return null;
    }

    private void extendViewPagerIfHidden() {
        if (!mTabsHaveLoaded) {
            /* extend ProgressBar */
            scrollToPositionAndLimitScrollingPastIt(findViewById(R.id.progressViewPager).getMeasuredHeight());
            return;
        }
        if (mPager.getVisibility() == View.INVISIBLE) {
            mPager.setVisibility(View.VISIBLE);
            scrollToPositionAndLimitScrollingPastIt(mExtensionHeightOnHeaderButtonClick);
        }
    }

    private void scrollToPositionAndLimitScrollingPastIt(final int position) {
        mBottomBarScroll.post(new Runnable() {
            @Override
            public void run() {
                if (mBottomBarScroll.getMinScrollLimit() > position) {
                    mBottomBarScroll.setMinScrollLimit(position);
                }
                mBottomBarScroll.smoothScrollTo(0, position);
            }
        });
        /* 500ms is a random value taken to allow the smoothScrollTo to finish animation before
         * placing a limit, since the latter blocks not only user interaction, but smoothScrollTo as well.
         * this hack is shorter and (i dare say) more elegant than overriding onTouchEvent of ScrollView. */
        mBottomBarScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position == 0) {
                    mPager.setVisibility(View.INVISIBLE);
                }
                mBottomBarScroll.setMinScrollLimit(position);
            }
        }, 500);
    }

    private void deselectButtonsOtherThan(View v) {
        for (int layoutId : TABS.keySet()) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            if (!ll.equals(v)) {
                ll.setSelected(false);
            }
        }
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);

        mBottomBarScroll = (ImageBottomBarScrollView) view.findViewById(R.id.bottomBarScrollLayout);
        mPager = (ViewPager) findViewById(R.id.bottomTabsPager);

        for (int layoutId : TABS.keySet()) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            ll.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    deselectButtonsOtherThan(v);
                    return false;
                }
            });
            ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleButton(v);
                }
            });
        }

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /* TODO: insert animated transition for the button color */
            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout ll = (LinearLayout) findViewById(
                        TABS.inverse().get(ImageBottomBarTabAdapter.ImageBottomBarTab.fromId(position)));
                ll.setSelected(true);
                deselectButtonsOtherThan(ll);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setUpViewPager(DerpibooruImageInfo content) {
        mPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager, content,
           new ImageBottomBarTabAdapter.ImageBottomBarTabHandler() {
               @Override
               public void onTabHeightProvided(ImageBottomBarTabAdapter.ImageBottomBarTab tab, int newHeight) {
                   /* check if the ProgressBar is still visible, i.e. the content has just loaded */
                   if (findViewById(R.id.progressViewPager).getVisibility() == View.VISIBLE) {
                       findViewById(R.id.progressViewPager).setVisibility(View.GONE);
                       mTabsHaveLoaded = true;
                       navigateViewPagerToTheCurrentlySelectedTab();
                   }
                   if (tab == getCurrentTab()) {
                       findViewById(R.id.bottomTabsPager).getLayoutParams().height = newHeight;
                       findViewById(R.id.bottomTabsPager).requestLayout();
                   }
               }
           }));
    }

    public interface TransparentOverlayTouchHandler {
        void onTouch(MotionEvent event);
    }
}
