package derpibooru.derpy.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

class ImageBottomBarViewPagerLayout extends FrameLayout {
    private static final BiMap<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> TABS =
            ImmutableBiMap.<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab>builder()
                    .put(R.id.buttonInfo, ImageBottomBarTabAdapter.ImageBottomBarTab.ImageInfo)
                    .put(R.id.buttonFaves, ImageBottomBarTabAdapter.ImageBottomBarTab.Faves)
                    .put(R.id.buttonComments, ImageBottomBarTabAdapter.ImageBottomBarTab.Comments).build();

    private FragmentManager mFragmentManager;
    private ViewPager mPager;
    private View mTransparentOverlay;

    private int mExtensionHeightOnHeaderButtonClick;
    private int mMaximumExtension;

    public ImageBottomBarViewPagerLayout(Context context) {
        super(context);
    }

    public ImageBottomBarViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBottomBarViewPagerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
    }

    public void setBarExtensionAttrs(int maximumBarHeight) {
        int transparentOverlayHeight =
                maximumBarHeight - findViewById(R.id.bottomBarHeaderLayout).getMeasuredHeight();;
        mTransparentOverlay.getLayoutParams().height = transparentOverlayHeight;
        mTransparentOverlay.requestLayout();

        mMaximumExtension = transparentOverlayHeight;
        mExtensionHeightOnHeaderButtonClick = transparentOverlayHeight / 2;
    }

    private void toggleButton(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            extendViewPager(isViewPagerFullyExtended());
            navigateViewPagerToTheCurrentlySelectedTab();
        } else if (!isViewPagerFullyExtended()) {
            extendViewPager(true);
        } else {
            v.setSelected(false);
            collapseViewPager();
        }
    }

    private void extendViewPager(boolean extendToMax) {
        if (mPager.getVisibility() == View.INVISIBLE) {
            mPager.setVisibility(View.VISIBLE);
        }
        new ViewPagerHeightChange()
                .to(extendToMax ? mMaximumExtension : mExtensionHeightOnHeaderButtonClick)
                .animate();
    }

    private void collapseViewPager() {
        new ViewPagerHeightChange()
                .to(0)
                /* going from fully-extended length to 0 equals twice the usual half-extension */
                .multiplyDurationBy(2)
                .doOnFinish(new Runnable() {
                    @Override
                    public void run() {
                        mPager.setVisibility(View.INVISIBLE);
                    }
                })
                .animate();
    }

    private void navigateViewPagerToTheCurrentlySelectedTab() {
        if (getCurrentTab() != null) {
            if (mPager.getVisibility() == View.INVISIBLE) {
                mPager.setVisibility(View.VISIBLE);
            }
            mPager.setCurrentItem(getCurrentTab().id(), true);
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

    private void deselectButtonsOtherThan(View v) {
        for (int layoutId : TABS.keySet()) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            if (!ll.equals(v)) {
                ll.setSelected(false);
            }
        }
    }

    protected void inflateLayout() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);

        mTransparentOverlay = findViewById(R.id.transparentOverlay);

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

        mPager = (ViewPager) findViewById(R.id.bottomTabsPager);
        mPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /* TODO: insert animated transition for the button color (TabLayout-like) */
                /* note: value/effort ratio for that is too low, not going to happen */
            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout ll = (LinearLayout) findViewById(TABS.inverse().get(ImageBottomBarTabAdapter.ImageBottomBarTab.fromId(position)));
                ll.setSelected(true);
                deselectButtonsOtherThan(ll);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    protected void populateViewPagerTabsWithImageInfo(DerpibooruImageInfo content) {
        ((ImageBottomBarTabAdapter) mPager.getAdapter()).setTabInfo(content);
    }

    private boolean isViewPagerFullyExtended() {
        return mTransparentOverlay.getMeasuredHeight() == 0;
    }

    private class ViewPagerHeightChange {
        private int mAnimationDuration = 200;
        private int mTargetHeight;
        private Runnable mPostAnimationRunnable;

        public ViewPagerHeightChange to(int height) {
            mTargetHeight = height;
            return this;
        }

        public ViewPagerHeightChange doOnFinish(Runnable runnable) {
            mPostAnimationRunnable = runnable;
            return this;
        }

        public ViewPagerHeightChange multiplyDurationBy(int m) {
            mAnimationDuration *= m;
            return this;
        }

        private int calculateTargetOverlayHeight() {
            return mMaximumExtension - mTargetHeight;
        }

        private int getCurrentOverlayHeight() {
            return mTransparentOverlay.getMeasuredHeight();
        }

        private void animate() {
            ValueAnimator va = ValueAnimator.ofInt(getCurrentOverlayHeight(), calculateTargetOverlayHeight());
            va.setDuration(mAnimationDuration);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int overlayHeight = (Integer) animation.getAnimatedValue();
                    mTransparentOverlay.getLayoutParams().height = overlayHeight;
                    mTransparentOverlay.requestLayout();
                    mPager.getLayoutParams().height = mMaximumExtension - overlayHeight;
                    mPager.requestLayout();
                }
            });
            va.start();
            if (mPostAnimationRunnable != null) {
                mTransparentOverlay.postDelayed(mPostAnimationRunnable, mAnimationDuration);
            }
        }
    }
}
