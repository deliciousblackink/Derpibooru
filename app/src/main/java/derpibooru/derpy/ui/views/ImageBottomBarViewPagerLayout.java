package derpibooru.derpy.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

class ImageBottomBarViewPagerLayout extends FrameLayout {
    private static final BiMap<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> TABS =
            ImmutableBiMap.<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab>builder()
                    .put(R.id.buttonInfo, ImageBottomBarTabAdapter.ImageBottomBarTab.ImageInfo)
                    .put(R.id.buttonFave, ImageBottomBarTabAdapter.ImageBottomBarTab.Faves)
                    .put(R.id.buttonComments, ImageBottomBarTabAdapter.ImageBottomBarTab.Comments).build();

    @Bind(R.id.transparentOverlay) View transparentOverlay;
    @Bind(R.id.bottomTabsPager) ViewPager tabPager;

    private FragmentManager mFragmentManager;

    private int mExtensionHeightOnHeaderButtonClick;
    private int mMaximumExtension;

    ImageBottomBarViewPagerLayout(Context context) {
        super(context);
    }

    ImageBottomBarViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    ImageBottomBarViewPagerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initializeWithFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
        inflateLayout();
    }

    public void setBarExtensionAttrs(int maximumBarHeight) {
        int transparentOverlayHeight =
                maximumBarHeight - findViewById(R.id.bottomBarHeaderLayout).getMeasuredHeight();
        transparentOverlay.getLayoutParams().height = transparentOverlayHeight;
        transparentOverlay.requestLayout();

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
        if (tabPager.getVisibility() == View.INVISIBLE) {
            tabPager.setVisibility(View.VISIBLE);
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
                        tabPager.setVisibility(View.INVISIBLE);
                    }
                })
                .animate();
    }

    private void navigateViewPagerToTheCurrentlySelectedTab() {
        if (getCurrentTab() != null) {
            if (tabPager.getVisibility() == View.INVISIBLE) {
                tabPager.setVisibility(View.VISIBLE);
            }
            tabPager.setCurrentItem(getCurrentTab().id(), true);
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

    private void deselectButtonsOtherThan(@Nullable View view) {
        for (int layoutId : TABS.keySet()) {
            AccentColorIconButton button = (AccentColorIconButton) findViewById(layoutId);
            if (view == null || !button.equals(view)) {
                button.setSelected(false);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int layoutId : TABS.keySet()) {
            findViewById(layoutId).setEnabled(enabled);
        }
    }

    protected void inflateLayout() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);
        ButterKnife.bind(this, view);
        deselectButtonsOtherThan(null); /* deselect all buttons */
        setButtonsEnabled(false); /* until the 'initializeTabs' method is called with tab information */

        tabPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager));
    }

    @OnPageChange(R.id.bottomTabsPager)
    void selectButtonAccordinglyToPageSelected(int position) {
        if (tabPager.getVisibility() == View.VISIBLE) {
            AccentColorIconButton button = (AccentColorIconButton) findViewById(
                    TABS.inverse().get(ImageBottomBarTabAdapter.ImageBottomBarTab.fromId(position)));
            button.setSelected(true);
            deselectButtonsOtherThan(button);
        }
    }

    protected void initializeTabs(DerpibooruImageDetailed content) {
        setButtonsEnabled(true);
        ((ImageBottomBarTabAdapter) tabPager.getAdapter()).setTabInfo(content);
        /* set button listeners */
        for (int layoutId : TABS.keySet()) {
            if (layoutId == R.id.buttonFave) continue; /* buttonFave has custom listeners defined outside this class */
            AccentColorIconButton button = (AccentColorIconButton) findViewById(layoutId);
            button.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    deselectButtonsOtherThan(v);
                    return false;
                }
            });
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleButton(v);
                }
            });
        }
    }

    private boolean isViewPagerFullyExtended() {
        return transparentOverlay.getMeasuredHeight() == 0;
    }

    private class ViewPagerHeightChange {
        private int mAnimationDuration = 200;
        private int mTargetHeight;
        private Runnable mPostAnimationRunnable;

        ViewPagerHeightChange to(int height) {
            mTargetHeight = height;
            return this;
        }

        ViewPagerHeightChange doOnFinish(Runnable runnable) {
            mPostAnimationRunnable = runnable;
            return this;
        }

        ViewPagerHeightChange multiplyDurationBy(int m) {
            mAnimationDuration *= m;
            return this;
        }

        private int calculateTargetOverlayHeight() {
            return mMaximumExtension - mTargetHeight;
        }

        private int getCurrentOverlayHeight() {
            return transparentOverlay.getMeasuredHeight();
        }

        private void animate() {
            ValueAnimator va = ValueAnimator.ofInt(getCurrentOverlayHeight(), calculateTargetOverlayHeight());
            va.setDuration(mAnimationDuration);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int overlayHeight = (Integer) animation.getAnimatedValue();
                    transparentOverlay.getLayoutParams().height = overlayHeight;
                    transparentOverlay.requestLayout();
                    tabPager.getLayoutParams().height = mMaximumExtension - overlayHeight;
                    tabPager.requestLayout();
                }
            });
            va.start();
            if (mPostAnimationRunnable != null) {
                transparentOverlay.postDelayed(mPostAnimationRunnable, mAnimationDuration);
            }
        }
    }
}
