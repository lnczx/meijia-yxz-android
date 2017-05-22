package com.meijialife.simi.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelpes;

/**
 * Indicator TabBar
 *
 * @author Andy
 * @since 2015-05-12
 * <p>
 * Thanks for https://github.com/keithellis/MaterialWidget
 */
public class IndicatorTabBars extends HorizontalScrollView {

    private int mMaxColumn;
    private static final int Default_Column = 3;

    private int mTextSize;
    private int mTextColor;
    private int mTextSelectedColor;

    private int mUnderLineColor;
    private int mUnderLineHeight;

    private TabContainer mTabContainer;
    private TabView mCurrentTab;
    private List<TabView> mTabList = new ArrayList<TabView>();
    private Rect lineRect = new Rect();
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ListItemClickHelpes callBack;

    /**
     * the ViewPager used with the IndicatorTabBar, not necessary, you could use the IndicatorTabBar individually
     */
    private ViewPager mViewPager;
    private Context context;

    public IndicatorTabBars(Context context) {
        this(context, null);
    }

    public IndicatorTabBars(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorTabBars(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        this.context = context;
        mTabContainer = new TabContainer(context);
        mTabContainer.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);// default
        addView(mTabContainer);

        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.IndicatorTabBar);
        mTextSize = attributes.getDimensionPixelSize(
                R.styleable.IndicatorTabBar_tab_text_size, getResources()
                        .getDimensionPixelSize(R.dimen.tab_text_size));

        mTextColor = attributes.getColor(
                R.styleable.IndicatorTabBar_tab_text_color, getResources()
                        .getColor(R.color.tab_text_color));
        mTextSelectedColor = attributes.getColor(
                R.styleable.IndicatorTabBar_tab_text_selected_color,
                getResources().getColor(R.color.simi_color_blue));

        mUnderLineColor = attributes.getColor(
                R.styleable.IndicatorTabBar_tab_underline_color, getResources()
                        .getColor(R.color.simi_color_blue));
        mUnderLineHeight = attributes.getDimensionPixelSize(
                R.styleable.IndicatorTabBar_tab_underline_height, getResources()
                        .getDimensionPixelSize(R.dimen.tab_underline_height));

        mMaxColumn = attributes.getInteger(
                R.styleable.IndicatorTabBar_tab_max_column, getResources()
                        .getInteger(R.integer.tab_max_column));

        attributes.recycle();

        linePaint.setStyle(Paint.Style.FILL);
    }

    public void initView(List<String> tabNames) {
        if (tabNames != null && tabNames.size() > 0) {
            initView(tabNames, mMaxColumn);
        }
    }

    public void initView(List<String> tabNames, int maxColumn) {
        if (maxColumn <= 0) {
            maxColumn = Default_Column;
        }
        int tabCount = tabNames.size();
        int screenWidth = getScreenWidth(getContext());
        //Divide equally
        final int tabWidth = Math.round(screenWidth / maxColumn);

        for (int i = 0; i < tabCount; i++) {
            addTabView(i, tabWidth, tabNames.get(i));
        }

        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    // The IndicatorTabBar's ScrollX and ScrollY both are 0 at first.
                    setTabSelected(position);
                    if (position >= mMaxColumn / 2) {
                        smoothScrollTo((position - (mMaxColumn / 2)) * tabWidth, 0);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    public void setMaxColumn(int column) {
        this.mMaxColumn = column;
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
    }

    public void setTextSelectedColor(int color) {
        this.mTextSelectedColor = color;
    }

    public void setUnderLineColor(int color) {
        this.mUnderLineColor = color;
    }

    public void setUnderLineHeight(int hight) {
        this.mUnderLineHeight = hight;
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
    }

    public ListItemClickHelpes getCallBack() {
        return callBack;
    }

    public void setCallBack(ListItemClickHelpes callBack) {
        this.callBack = callBack;
    }

    /**
     * add the TabView to TabContainer
     *
     * @param index tab's index
     * @param width tab's width
     * @param title tab's title
     */
    private void addTabView(final int index, int width, String title) {
        TabView tabView = new TabView(getContext());
        tabView.setIndex(index);
        tabView.setText(title);
        tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tabView.setTextColor(mTextColor);
        tabView.setWidth(width);
        tabView.setTabWidth(width);

        tabView.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tabView) {
                int index = tabView.getIndex();
                mCurrentTab = tabView;
                mTabContainer.postInvalidate();
                String typeId = "0";
                if (index == 0) {//最新
                    typeId = "0";
                } else if (index == 1) {//我的发布的
                    typeId = "1";

                    User user = DBHelper.getUser(context);
                    if (user == null) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                        return;
                    }
                } else if (index == 2) {//悬赏
                    typeId = "2";
                } else if (index == 3) {//精选
                    typeId = "3";
                }
                callBack.onClick(typeId);
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });

        if (index == 0) {
            mCurrentTab = tabView;
        }
        mTabList.add(tabView);
        mTabContainer.addView(tabView, new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * get the Screen Width in px
     *
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * set which tab is selected ,used for viewpager when onPageSelected
     *
     * @param position
     */
    private void setTabSelected(int position) {
        if (mTabList != null) {
            TabView currentTabView = mTabList.get(position);
            if (currentTabView != null) {
                currentTabView.performSelectAction();
            }
        }
    }

    /**
     * Tab container which extends LinearLayout as a ViewGroup for adding TabView
     */
    private class TabContainer extends LinearLayout {

        public TabContainer(Context context) {
            this(context, null);
        }

        public TabContainer(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public TabContainer(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            linePaint.setColor(mUnderLineColor);

            if (mCurrentTab != null) {
                for (TabView tabView : mTabList) {
                    tabView.setTextColor(mTextColor);
                }
                mCurrentTab.setTextColor(mTextSelectedColor);

                int x = mCurrentTab.getIndex() * mCurrentTab.getTabWidth();
                lineRect.left = mCurrentTab.getIndex() * mCurrentTab.getTabWidth();
                lineRect.top = getHeight() - mUnderLineHeight;
                lineRect.right = x + mCurrentTab.getWidth();
                lineRect.bottom = getHeight();

                canvas.drawRect(lineRect, linePaint);
            }
        }
    }

    /**
     * Tab Item which extends TextView
     */
    private class TabView extends TextView {

        /**
         * Tab's index
         */
        private int mIndex;
        /**
         * Tab's width
         */
        private int mTabWidth;

        private OnTabSelectedListener mOnTabSelectedListener;

        public TabView(Context context) {
            this(context, null);
        }

        public TabView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public TabView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            setGravity(Gravity.CENTER);
            setBackgroundColor(Color.TRANSPARENT);
        }

        public int getIndex() {
            return mIndex;
        }

        public void setIndex(int index) {
            this.mIndex = index;
        }

        public int getTabWidth() {
            return mTabWidth;
        }

        public void setTabWidth(int width) {
            this.mTabWidth = width;
        }

        public void setOnTabSelectedListener(OnTabSelectedListener listener) {
            this.mOnTabSelectedListener = listener;
        }

        public void performSelectAction() {
            if (mOnTabSelectedListener != null) {
                mOnTabSelectedListener.onTabSelected(TabView.this);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    performSelectAction();
                    break;
            }
            return true;
        }

    }

    /**
     * the interface will response when a Tab is selected
     */
    public interface OnTabSelectedListener {
        void onTabSelected(TabView tabView);
    }


}
