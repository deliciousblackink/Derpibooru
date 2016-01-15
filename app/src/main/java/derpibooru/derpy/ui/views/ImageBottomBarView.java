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
import derpibooru.derpy.ui.adapters.ImageBottomBarTabAdapter;

public class ImageBottomBarView extends FrameLayout {
    private static final int[] LAYOUT_BUTTONS = {
            R.id.buttonInfo,
            R.id.buttonFaves,
            R.id.buttonComments };

    private ViewPager mPager;
    private FragmentManager mFragmentManager;
    private ImageBottomBarViewHandler mViewHandler;
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

    public ImageBottomBarView setBottomToolbarViewHandler(ImageBottomBarViewHandler handler) {
        mViewHandler = handler;
        return this;
    }

    /* To be removed */
    public void setInfo(int faves, int comments) {
        init();
        TextView d = (TextView) this.findViewById(R.id.textFaves);
        d.setText(Integer.toString(faves));
        TextView u = (TextView) this.findViewById(R.id.textComments);
        u.setText(Integer.toString(comments));
    }

    public void onButtonTouched(View v) {
        /* deselect other buttons */
        for (int layoutId : LAYOUT_BUTTONS) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            if (!ll.equals(v)) {
                ll.setSelected(false);
            }
        }
    }

    public void onButtonClicked(View v) {
        if (mPager == null) {
            initViewPager();
        }

        if (!v.isSelected()) {
            v.setSelected(true);
            /* show ViewPager */
            if (mPager.getVisibility() == View.GONE) {
                mPager.setVisibility(View.VISIBLE);
            }
            if (!mIsExtended) {
                mIsExtended = true;
                mViewHandler.showBottomToolbarWithTabs();
            }

            /* navigate ViewPager to the corresponding tab */
            switch (v.getId()) {
                case R.id.buttonInfo:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.ImageInfo.getID(),
                            true);
                    break;
                case R.id.buttonFaves:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.Faves.getID(),
                            true);
                    break;
                case R.id.buttonComments:
                    mPager.setCurrentItem(ImageBottomBarTabAdapter.ImageBottomBarTabs.Comments.getID(),
                            true);
                    break;
            }
        } else {
            v.setSelected(false);
            /* clicking on the active button hides the ViewPager */
            mPager.setVisibility(View.GONE);
            mViewHandler.showBottomToolbarOnly();
            mIsExtended = false;
        }
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_image_bottom_bar, null);
        addView(view);

        /* TODO: a cleaner solution */
        for (int layoutId : LAYOUT_BUTTONS) {
            LinearLayout ll = (LinearLayout) findViewById(layoutId);
            ll.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onButtonTouched(v);
                    return false;
                }
            });
            ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClicked(v);
                }
            });
        }
    }

    private void initViewPager() {
        /* set up ViewPager */
        mPager = (ViewPager) findViewById(R.id.bottomTabsPager);
        mPager.setAdapter(new ImageBottomBarTabAdapter(mFragmentManager));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /* TODO: insert transition for the button color */
            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout ll = (LinearLayout) findViewById(LAYOUT_BUTTONS[position]);
                ll.setSelected(true);
                onButtonTouched(ll); /* deselect other buttons */
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
