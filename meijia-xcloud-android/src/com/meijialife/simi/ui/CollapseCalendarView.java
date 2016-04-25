package com.meijialife.simi.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.fra.Home1Fra;
import com.meijialife.simi.ui.calendar.CalendarManager;
import com.meijialife.simi.ui.calendar.Day;
import com.meijialife.simi.ui.calendar.DayView;
import com.meijialife.simi.ui.calendar.Formatter;
import com.meijialife.simi.ui.calendar.Month;
import com.meijialife.simi.ui.calendar.ResizeManager;
import com.meijialife.simi.ui.calendar.Week;
import com.meijialife.simi.ui.calendar.WeekView;
import com.meijialife.simi.utils.CalendarUtils;

/**
 * Created by Blaz Solar on 28/02/14.
 */
public class CollapseCalendarView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "CalendarView";
    private VelocityTracker mVelocityTracker;
    public int mTouchState = TOUCH_STATE_REST;

    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    /** 
     * 左右滑动切换月份时，手指滑动需要达到的速度。 
     */ 
    private static final int SNAP_VELOCITY = 350;

    @Nullable
    private CalendarManager mManager;
    private Context mContext;
    private Home1Fra mHome;

    @NonNull
    private TextView mTitleView;
    @NonNull
    private ImageButton mPrev;
    @NonNull
    private ImageButton mNext;
    @NonNull
    private LinearLayout mWeeksView;
    
    @NonNull
    private TextView mCalendarYear;//当前年份
    @NonNull
    private TextView mCalendarMonth;//当前月份
    @NonNull
    private TextView mCalendarWeek;//当前星期
    
    private ImageView bottom;//底部提示箭头

    @NonNull
    private final LayoutInflater mInflater;
    @NonNull
    private final RecycleBin mRecycleBin = new RecycleBin();

    @Nullable
    private OnDateSelect mListener;

    @NonNull
    private TextView mSelectionText;
    @NonNull
    private LinearLayout mHeader;

    @NonNull
    private ResizeManager mResizeManager;

    private boolean initialized;

    public CollapseCalendarView(Context context) {
        this(context, null);
    }

    public CollapseCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarViewStyle);
    }

    @SuppressLint("NewApi")
    public CollapseCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mInflater = LayoutInflater.from(context);

        mResizeManager = new ResizeManager(this);

        inflate(context, R.layout.calendar_layout, this);

        setOrientation(VERTICAL);
    }

    public void init(@NonNull CalendarManager manager ,Context context, Home1Fra home) {
        if (manager != null) {

            mManager = manager;
            mContext = context;
            mHome = home;

            populateLayout();

            if (mListener != null) {
                mListener.onDateSelected(mManager.getSelectedDay());
            }

        }
    }

    @Nullable
    public CalendarManager getManager() {
        return mManager;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "On click");
        if (mManager != null) {
            int id = v.getId();
            if (id == R.id.prev) {
                if (mManager.prev()) {
                    populateLayout();
                }
            } else if (id == R.id.next) {
                Log.d(TAG, "next");
                if (mManager.next()) {
                    Log.d(TAG, "populate");
                    populateLayout();
                }
            }

        }
    }

    @SuppressLint("WrongCall")
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        mResizeManager.onDraw();

        super.dispatchDraw(canvas);
    }

    @Nullable
    public CalendarManager.State getState() {
        if (mManager != null) {
            return mManager.getState();
        } else {
            return null;
        }
    }

    public void setListener(@Nullable OnDateSelect listener) {
        mListener = listener;
    }

    /**
     * @deprecated This will be removed
     */
    @Deprecated
    public void setTitle(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            mHeader.setVisibility(View.VISIBLE);
            mSelectionText.setVisibility(View.GONE);
        } else {
            mHeader.setVisibility(View.GONE);
            mSelectionText.setVisibility(View.VISIBLE);
            mSelectionText.setText(text);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mResizeManager.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            // Remember where the motion event started
            mLastMotionX = x;
            break;
        case MotionEvent.ACTION_MOVE:
//            if (mTouchState == TOUCH_STATE_SCROLLING) {
//                // Scroll to follow the motion event
//                final int deltaX = (int) (mLastMotionX - x);
//                mLastMotionX = x;
//
//                if (deltaX < 0) {
//                    if (deltaX + getScrollX() >= -getChildAt(0).getWidth()) {
//                        scrollBy(deltaX, 0);
//                    }
//
//                } else if (deltaX > 0) {
//                    final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getScrollX() - getWidth();
//
//                    if (availableToScroll > 0) {
//                        scrollBy(Math.min(availableToScroll, deltaX), 0);
//                    }
//                }
//            }
            break;
        case MotionEvent.ACTION_UP:
            mTouchState = TOUCH_STATE_REST;
            break;
        case MotionEvent.ACTION_CANCEL:
//            if (mTouchState == TOUCH_STATE_SCROLLING) {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();

                if (velocityX > SNAP_VELOCITY) {
                    // Fling hard enough to move left
                    // 上一月
                    if (mManager.prev()) {
                        populateLayout();
                    }

                } else if (velocityX < -SNAP_VELOCITY) {
                    // Fling hard enough to move right 下一月
                    if (mManager.next()) {
                        Log.d(TAG, "populate");
                        populateLayout();
                    }
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
//            }
            mTouchState = TOUCH_STATE_REST;
        }
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        // 日历滑动，有bug
//         if (mVelocityTracker == null) {
//         mVelocityTracker = VelocityTracker.obtain();
//         }
//         mVelocityTracker.addMovement(ev);
//        
//         final int action = ev.getAction();
//         final float x = ev.getX();
//        
//         switch (action) {
//         case MotionEvent.ACTION_DOWN:
//        
//         // Remember where the motion event started
//         mLastMotionX = x;
//         break;
//         case MotionEvent.ACTION_MOVE:
//         if (mTouchState == TOUCH_STATE_SCROLLING) {
//         // Scroll to follow the motion event
//         final int deltaX = (int) (mLastMotionX - x);
//         mLastMotionX = x;
//        
//         if (deltaX < 0) {
//         if (deltaX + getScrollX() >= -getChildAt(0).getWidth()) {
//         scrollBy(deltaX, 0);
//         }
//        
//         } else if (deltaX > 0) {
//         final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getScrollX() - getWidth();
//        
//         if (availableToScroll > 0) {
//         scrollBy(Math.min(availableToScroll, deltaX), 0);
//         }
//         }
//         }
//         break;
//         case MotionEvent.ACTION_UP:
//         if (mTouchState == TOUCH_STATE_SCROLLING) {
//         final VelocityTracker velocityTracker = mVelocityTracker;
//         velocityTracker.computeCurrentVelocity(1000);
//         int velocityX = (int) velocityTracker.getXVelocity();
//        
//         if (velocityX > SNAP_VELOCITY) {
//         // Fling hard enough to move left
//         // 上一月
//         if (mManager.prev()) {
//         populateLayout();
//         }
//        
//         } else if (velocityX < -SNAP_VELOCITY) {
//         // Fling hard enough to move right 下一月
//         if (mManager.next()) {
//         Log.d(TAG, "populate");
//         populateLayout();
//         }
//         }
//        
//         if (mVelocityTracker != null) {
//         mVelocityTracker.recycle();
//         mVelocityTracker = null;
//         }
//         }
//         mTouchState = TOUCH_STATE_REST;
//         break;
//         case MotionEvent.ACTION_CANCEL:
//         mTouchState = TOUCH_STATE_REST;
//         }
        //
        // return true;

        super.onTouchEvent(ev);
        return mResizeManager.onTouchEvent(ev);
//        return false;//屏蔽首页上下滑动 
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitleView = (TextView) findViewById(R.id.title);
        mPrev = (ImageButton) findViewById(R.id.prev);
        mNext = (ImageButton) findViewById(R.id.next);
        mWeeksView = (LinearLayout) findViewById(R.id.weeks);
        bottom = (ImageView)findViewById(R.id.bottom);

        
        mCalendarYear = (TextView) findViewById(R.id.m_calendar_year);
        mCalendarMonth = (TextView) findViewById(R.id.m_calendar_month);
        mCalendarWeek = (TextView) findViewById(R.id.m_calendar_week);
        
        mCalendarYear.setText(CalendarUtils.getCurrentYear()+"年");
        mCalendarMonth.setText(CalendarUtils.getCurrentMonth()+"月");
        mCalendarWeek.setText(CalendarUtils.getWeeks());

        
        mHeader = (LinearLayout) findViewById(R.id.header);
        mSelectionText = (TextView) findViewById(R.id.selection_title);

        mPrev.setOnClickListener(this);
        mNext.setOnClickListener(this);

        populateLayout();
    }

    private void populateDays() {

        if (!initialized) {
            CalendarManager manager = getManager();

            if (manager != null) {
                Formatter formatter = manager.getFormatter();

                LinearLayout layout = (LinearLayout) findViewById(R.id.days);

                LocalDate date = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
                for (int i = 0; i < 7; i++) {
                    TextView textView = (TextView) layout.getChildAt(i);
//                    textView.setText(formatter.getDayName(date));
                    textView.setText(getWeekName(i));

                    date = date.plusDays(1);
                }

                initialized = true;
            }
        }

    }
    
    /**
     * 得到星期几的文字 garry
     * @param i
     * @return
     */
    private String getWeekName(int i){
        String weekName = "";
        switch (i) {
        case 0:
            weekName = "一";
            break;
        case 1:
            weekName = "二";
            break;
        case 2:
            weekName = "三";
            break;
        case 3:
            weekName = "四";
            break;
        case 4:
            weekName = "五";
            break;
        case 5:
            weekName = "六";
            break;
        case 6:
            weekName = "日";
            break;

        default:
            break;
        }
        
        return weekName;
    }

    public void populateLayout() {
        
        if(mHome != null){
            mHome.getTotalByMonth();
        }

        if (mManager != null) {

            populateDays();

            mPrev.setEnabled(mManager.hasPrev());
            mNext.setEnabled(mManager.hasNext());

            try {
                //add by andye ,2015.12.26
               /* String titleDay=mManager.getHeaderText();
                String monthString=titleDay.replaceAll("年", ".").replaceAll("月", "");
                mTitleView.setText(monthString);*/
                mCalendarWeek.setText(mManager.getWeekOfMonth());
            } catch (Exception e) {
                mTitleView.setText("");
                e.printStackTrace();
            }

            if (mManager.getState() == CalendarManager.State.MONTH) {
                populateMonthLayout((Month) mManager.getUnits());
                bottom.setBackgroundResource(R.drawable.rili_arrow_up);
            } else {
                populateWeekLayout((Week) mManager.getUnits());
                bottom.setBackgroundResource(R.drawable.rili_arrow_down);
            }
        }

    }
    
    /**
     * 刷新日历VIEW add by garry
     */
    public void updateUI() {
        
        if (mManager != null) {
            
            populateDays();
            
            mPrev.setEnabled(mManager.hasPrev());
            mNext.setEnabled(mManager.hasNext());
            
            try {//add by andye ,2015.12.26
           /*     String titleDay=mManager.getHeaderText();
                String monthString=titleDay.replaceAll("年", ".").replaceAll("月", "");
                mTitleView.setText(monthString);*/
                mCalendarWeek.setText(mManager.getWeekOfMonth());            
            } catch (Exception e) {
                mTitleView.setText("");
                e.printStackTrace();
            }
            
            if (mManager.getState() == CalendarManager.State.MONTH) {
                populateMonthLayout((Month) mManager.getUnits());
            } else {
                populateWeekLayout((Week) mManager.getUnits());
            }
        }
        
    }

    private void populateMonthLayout(Month month) {

        List<Week> weeks = month.getWeeks();
        int cnt = weeks.size();
        for (int i = 0; i < cnt; i++) {
            WeekView weekView = getWeekView(i);
            populateWeekLayout(weeks.get(i), weekView);
        }

        int childCnt = mWeeksView.getChildCount();
        if (cnt < childCnt) {
            for (int i = cnt; i < childCnt; i++) {
                cacheView(i);
            }
        }

    }

    private void populateWeekLayout(Week week) {
        WeekView weekView = getWeekView(0);
        populateWeekLayout(week, weekView);

        int cnt = mWeeksView.getChildCount();
        if (cnt > 1) {
            for (int i = cnt - 1; i > 0; i--) {
                cacheView(i);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
	private void populateWeekLayout(@NonNull Week week, @NonNull WeekView weekView) {

        List<Day> days = week.getDays();
        for (int i = 0; i < 7; i++) {
            final Day day = days.get(i);
            // DayView dayView = (DayView) weekView.getChildAt(i);
            LinearLayout layout = (LinearLayout) weekView.getChildAt(i);
            DayView dayView = (DayView) layout.findViewById(R.id.tvDayView);
            DayView tvChinaDay = (DayView) layout.findViewById(R.id.tvChinaDay);
            View view_today = layout.findViewById(R.id.view_today);
            
            View buttomMark = layout.findViewById(R.id.buttomMark);
            String date = day.getTextLong();
            if(DBHelper.isCalendarMark(mContext, date)){
                //如果当天有数据，则显示圆点标记
                buttomMark.setVisibility(View.VISIBLE);
            }else{
                buttomMark.setVisibility(View.INVISIBLE);
            }
            if(day.isToday()){
                //如果是今天，显示红色大圆圈
//                view_today.setVisibility(View.VISIBLE);
                dayView.setTextColor(getResources().getColor(R.color.text_calendar_today));
            }else{
//                view_today.setVisibility(View.GONE);
                dayView.setTextColor(getResources().getColor(R.color.text_calendar));
            }
          
            dayView.setText(day.getText());
            layout.setSelected(day.isSelected());
            dayView.setCurrent(day.isCurrent());
            tvChinaDay.setText(day.getChinaDate());
            tvChinaDay.setCurrent(day.isCurrent());
            boolean enables = day.isEnabled();
            dayView.setEnabled(enables);
            if (enables) { // 解除点击限制，所有的都可以点击
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalDate date = day.getDate();
                        if (mManager.selectDay(date)) {
                            populateLayout();
                            if (mListener != null) {
                                mListener.onDateSelected(date);
                            }
                        }
                    }
                });
            } else {
                dayView.setOnClickListener(null);
            }
            
         
            
        }

    }

    @NonNull
    public LinearLayout getWeeksView() {
        return mWeeksView;
    }

    @NonNull
    private WeekView getWeekView(int index) {
        int cnt = mWeeksView.getChildCount();

        if (cnt < index + 1) {
            for (int i = cnt; i < index + 1; i++) {
                View view = getView();
                mWeeksView.addView(view);
            }
        }

        return (WeekView) mWeeksView.getChildAt(index);
    }

    private View getView() {
        View view = mRecycleBin.recycleView();
        if (view == null) {
            view = mInflater.inflate(R.layout.week_layout, this, false);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void cacheView(int index) {
        View view = mWeeksView.getChildAt(index);
        if (view != null) {
            mWeeksView.removeViewAt(index);
            mRecycleBin.addView(view);
        }
    }

    public LocalDate getSelectedDate() {
        return mManager.getSelectedDay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mResizeManager.recycle();
    }

    private class RecycleBin {

        private final Queue<View> mViews = new LinkedList<View>();

        @Nullable
        public View recycleView() {
            return mViews.poll();
        }

        public void addView(@NonNull View view) {
            mViews.add(view);
        }

    }

    public interface OnDateSelect {
        public void onDateSelected(LocalDate date);
    }

}
