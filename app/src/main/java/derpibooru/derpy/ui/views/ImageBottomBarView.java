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

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInfo;
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;
import derpibooru.derpy.ui.animations.ImageBottomBarAnimator;

public class ImageBottomBarView extends FrameLayout {
    private static final int[] LAYOUT_BUTTONS = {
            R.id.buttonInfo,
            R.id.buttonFaves,
            R.id.buttonComments };

    private ViewPager mPager;
    private FragmentManager mFragmentManager;
    private ImageBottomBarAnimator mAnimator;
    private boolean mIsExtended = false;

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

    public ImageBottomBarView setViewAbove(View v) {
        mAnimator = new ImageBottomBarAnimator(v);
        return this;
    }

    public void setBasicInfo(int faves, int comments) {
        init();
        TextView d = (TextView) this.findViewById(R.id.textFaves);
        d.setText(Integer.toString(faves));
        TextView u = (TextView) this.findViewById(R.id.textComments);
        u.setText(Integer.toString(comments));
    }

    public void setTabInfo(DerpibooruImageInfo info) {
        setUpViewPager(info);
    }

    public void selectButton(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            /* show ViewPager */
            if (mPager.getVisibility() == View.GONE) {
                mPager.setVisibility(View.VISIBLE);
            }
            if (!mIsExtended) {
                mIsExtended = true;
                mAnimator.animateBottomBarExtension();
            }

            /* navigate ViewPager to the corresponding tab */
            switch (v.getId()) {
                case R.id.buttonInfo:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.ImageInfo.id(),
                            true);
                    break;
                case R.id.buttonFaves:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.Faves.id(),
                            true);
                    break;
                case R.id.buttonComments:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.Comments.id(),
                            true);
                    break;
            }
        } else {
            v.setSelected(false);
            mPager.setVisibility(View.GONE);
            mAnimator.animateBottomBarCompression();
            mIsExtended = false;
        }
    }

    public void deselectButtonsOtherThan(View v) {
        for (int layoutId : LAYOUT_BUTTONS) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            if (!ll.equals(v)) {
                ll.setSelected(false);
            }
        }
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);

        for (int layoutId : LAYOUT_BUTTONS) {
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
                    selectButton(v);
                }
            });
        }

        mPager = (ViewPager) findViewById(R.id.bottomTabsPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /* TODO: insert animated transition for the button color */
            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout ll = (LinearLayout) findViewById(LAYOUT_BUTTONS[position]);
                ll.setSelected(true);
                deselectButtonsOtherThan(ll);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setUpViewPager(DerpibooruImageInfo content) {
        mPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager, content));
    }
}
