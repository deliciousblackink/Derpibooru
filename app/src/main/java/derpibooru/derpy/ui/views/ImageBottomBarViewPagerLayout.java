package derpibooru.derpy.ui.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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
import derpibooru.derpy.ui.animators.ImageBottomBarAnimator;

class ImageBottomBarViewPagerLayout extends FrameLayout {
    private static final BiMap<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab> TABS =
            ImmutableBiMap.<Integer, ImageBottomBarTabAdapter.ImageBottomBarTab>builder()
                    .put(R.id.buttonInfo, ImageBottomBarTabAdapter.ImageBottomBarTab.ImageInfo)
                    .put(R.id.buttonFave, ImageBottomBarTabAdapter.ImageBottomBarTab.Faves)
                    .put(R.id.buttonComments, ImageBottomBarTabAdapter.ImageBottomBarTab.Comments).build();

    @Bind(R.id.bottomBarHeaderLayout) View tabPagerHeader;
    @Bind(R.id.transparentOverlay) View transparentOverlay;
    @Bind(R.id.bottomTabsPager) ViewPager tabPager;

    private ImageBottomBarAnimator mAnimator;
    private FragmentManager mFragmentManager;
    private ImageTagView.OnTagClickListener mTagListener;

    private ImageBottomBarAnimator.ExtensionState mRestoredState;

    ImageBottomBarViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeWithFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
        inflateLayout();
    }

    protected void inflateLayout() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);
        ButterKnife.bind(this, view);
        deselectButtonsOtherThan(null); /* deselect all buttons */
        setButtonsEnabled(false); /* until the 'initializeTabs' method is called with tab information */
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mAnimator = new ImageBottomBarAnimator(
                transparentOverlay, tabPager, tabPagerHeader, h);
        if (mRestoredState != null) {
            if (mRestoredState != ImageBottomBarAnimator.ExtensionState.None) {
                selectButtonAccordingToPageSelected(tabPager.getCurrentItem());
                mAnimator.doAfter(new Runnable() {
                    @Override
                    public void run() {
                        mAnimator.extendViewPager(mRestoredState, 0);
                    }
                });
            }
            mAnimator.extendViewPagerHeader(0);
        }
    }

    public void setTagListener(ImageTagView.OnTagClickListener listener) {
        mTagListener = listener;
    }

    protected void initializeTabs(DerpibooruImageDetailed content) {
        tabPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager, content) {
            @Override
            public void onTagClicked(int tagId) {
                mTagListener.onTagClicked(tagId);
            }
        });
        setButtonsEnabled(true);
        setButtonListeners();
        post(new Runnable() {
            @Override
            public void run() {
                if (mRestoredState == null) {
                    mAnimator.extendViewPagerHeader();
                }
            }
        });
    }

    private void toggleButton(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            if (mAnimator.getCurrentExtensionState() == ImageBottomBarAnimator.ExtensionState.None) {
                mAnimator.extendViewPager(ImageBottomBarAnimator.ExtensionState.HalfSize);
            }
            navigateViewPagerToTheCurrentlySelectedTab();
        } else if (mAnimator.getCurrentExtensionState() != ImageBottomBarAnimator.ExtensionState.Max) {
            mAnimator.extendViewPager(ImageBottomBarAnimator.ExtensionState.Max);
        } else {
            v.setSelected(false);
            mAnimator.collapseViewPager();
        }
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
            if ((view == null) || (!button.equals(view))) {
                button.setSelected(false);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int layoutId : TABS.keySet()) {
            findViewById(layoutId).setEnabled(enabled);
        }
    }

    @OnPageChange(R.id.bottomTabsPager)
    void selectButtonAccordingToPageSelected(int position) {
        if ((tabPager.getVisibility() == View.VISIBLE) || (mRestoredState != ImageBottomBarAnimator.ExtensionState.None)) {
            AccentColorIconButton button = (AccentColorIconButton) findViewById(
                    TABS.inverse().get(ImageBottomBarTabAdapter.ImageBottomBarTab.fromId(position)));
            button.setSelected(true);
            deselectButtonsOtherThan(button);
        }
    }

    private void setButtonListeners() {
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

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.extensionState = mAnimator.getCurrentExtensionState();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mRestoredState = ss.extensionState;
    }

    static class SavedState extends BaseSavedState {
        ImageBottomBarAnimator.ExtensionState extensionState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            extensionState = ImageBottomBarAnimator.ExtensionState.fromValue(in.readInt());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(extensionState.toValue());
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
