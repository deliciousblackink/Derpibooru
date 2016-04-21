package derpibooru.derpy.ui.views.imagedetailedview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.ui.adapters.CommentListAdapter;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;
import derpibooru.derpy.ui.animators.ImageDetailedViewAnimator;
import derpibooru.derpy.ui.views.AccentColorIconButton;

import static derpibooru.derpy.ui.animators.ImageDetailedViewAnimator.BottomBarExtensionState;

public class ImageBottomBarView extends LinearLayout {
    private static final String EXTRAS_BOTTOM_BAR_PAGER_CURRENT_TAB = "derpibooru.derpy.BottomBarViewPagerCurrentTab";

    private static final BiMap<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> TABS =
            ImmutableBiMap.<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab>builder()
                    .put(R.id.buttonInfo, ImageBottomBarTabAdapter.ImageBottomBarTab.ImageInfo)
                    .put(R.id.buttonFave, ImageBottomBarTabAdapter.ImageBottomBarTab.Faves)
                    .put(R.id.buttonComments, ImageBottomBarTabAdapter.ImageBottomBarTab.Comments).build();

    @Bind(R.id.buttonFave) AccentColorIconButton buttonFave;
    @Bind(R.id.buttonComments) AccentColorIconButton buttonComments;

    @Bind(R.id.bottomBarHeaderLayout) View tabPagerHeader;
    @Bind(R.id.bottomTabsPager) ViewPager tabPager;

    private BottomBarHandler mHandler;

    public ImageBottomBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(getContext(), R.layout.view_image_detailed_bottom_bar, null);
        addView(view);
        ButterKnife.bind(this, view);
        for (int layoutId : TABS.keySet()) {
            /* prevent icons from blending into the background by disabling tint toggle on touch */
            ((AccentColorIconButton) findViewById(layoutId)).setToggleIconTintOnTouch(false);
        }
    }

    public void initialize(FragmentManager tabFragmentManager,
                           ImageTagView.OnTagClickListener tagClickListener,
                           BottomBarHandler handler,
                           @Nullable Bundle savedInstanceState) {
        mHandler = handler;
        tabPager.setAdapter(getNewInstanceOfTabAdapter(tabFragmentManager, tagClickListener));
        refreshCommentCount(mHandler.getImage().getThumb().getCommentCount());
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(EXTRAS_BOTTOM_BAR_PAGER_CURRENT_TAB);
            tabPager.setCurrentItem(position);
            selectButtonAccordingToPageSelected(position);
        }
    }

    public void saveInstanceState(Bundle outState) {
        outState.putInt(EXTRAS_BOTTOM_BAR_PAGER_CURRENT_TAB, tabPager.getCurrentItem());
    }

    private ImageBottomBarTabAdapter getNewInstanceOfTabAdapter(FragmentManager tabFragmentManager,
                                                                ImageTagView.OnTagClickListener tagClickListener) {
        return new ImageBottomBarTabAdapter(tabFragmentManager,
                                            mHandler.getUserFilter(),
                                            mHandler.getImage(),
                                            tagClickListener,
                                            new CommentListAdapter.OnCommentCountChangeListener() {
            @Override
            public void onNewCommentsAdded(int commentsAdded) {
                mHandler.getImage().getThumb().increaseCommentCount(commentsAdded);
                refreshCommentCount(mHandler.getImage().getThumb().getCommentCount());
            }
        });
    }

    private void refreshCommentCount(int comments) {
        buttonComments.setText(Integer.toString(comments));
    }

    private ImageBottomBarTabAdapter.ImageBottomBarTab getCurrentTab() {
        for (Map.Entry<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> tab : TABS.entrySet()) {
            if (findViewById(tab.getKey()).isSelected()) {
                return tab.getValue();
            }
        }
        return null;
    }

    @OnPageChange(R.id.bottomTabsPager)
    void selectButtonAccordingToPageSelected(int position) {
        if (mHandler.getExtensionState() != BottomBarExtensionState.None) {
            AccentColorIconButton button = (AccentColorIconButton) findViewById(
                    TABS.inverse().get(ImageBottomBarTabAdapter.ImageBottomBarTab.fromId(position)));
            button.setSelected(true);
            deselectButtonsOtherThan(button);
        }
    }

    @OnClick({ R.id.buttonInfo, R.id.buttonComments })
    void toggleButton(View v) {
        deselectButtonsOtherThan(v);
        if (!v.isSelected()) {
            v.setSelected(true);
            if (mHandler.getExtensionState() == BottomBarExtensionState.None) {
                mHandler.changeExtensionState(BottomBarExtensionState.HalfSize);
            }
            navigateViewPagerToTheCurrentlySelectedTab();
        } else if (mHandler.getExtensionState() != BottomBarExtensionState.Max) {
            mHandler.changeExtensionState(BottomBarExtensionState.Max);
        } else {
            v.setSelected(false);
            mHandler.changeExtensionState(BottomBarExtensionState.None);
        }
    }

    private void deselectButtonsOtherThan(@Nullable View view) {
        for (int layoutId : TABS.keySet()) {
            AccentColorIconButton button = (AccentColorIconButton) findViewById(layoutId);
            if ((view == null) || (!button.equals(view))) {
                button.setSelected(false);
            }
        }
    }

    private void navigateViewPagerToTheCurrentlySelectedTab() {
        if (getCurrentTab() != null) {
            tabPager.setCurrentItem(getCurrentTab().id(), true);
        }
    }

    public interface BottomBarHandler {
        DerpibooruFilter getUserFilter();
        DerpibooruImageDetailed getImage();
        ImageDetailedViewAnimator.BottomBarExtensionState getExtensionState();
        void changeExtensionState(ImageDetailedViewAnimator.BottomBarExtensionState newState);
    }
}
