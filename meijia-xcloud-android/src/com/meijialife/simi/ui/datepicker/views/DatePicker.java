package com.meijialife.simi.ui.datepicker.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.AlarmListActivity;
import com.meijialife.simi.activity.MainPlusAffairActivity;
import com.meijialife.simi.fra.ScheduleFra;
import com.meijialife.simi.ui.datepicker.bizs.decors.DPDecor;
import com.meijialife.simi.ui.datepicker.bizs.languages.DPLManager;
import com.meijialife.simi.ui.datepicker.bizs.themes.DPTManager;
import com.meijialife.simi.ui.datepicker.cons.DPMode;
import com.meijialife.simi.ui.datepicker.utils.MeasureUtil;
import com.meijialife.simi.utils.Utils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * DatePicker
 *
 * @author AigeStudio 2015-06-29
 */
public class DatePicker extends LinearLayout {
    private DPTManager mTManager;// 主题管理器
    private DPLManager mLManager;// 语言管理器

    private MonthView monthView;// 月视图
    private TextView tvYear, tvMonth;// 年份 月份显示
    private TextView tvEnsure;// 确定按钮显示


    private OnDateSelectedListener onDateSelectedListener;// 日期多选后监听

    /**
     * 日期单选监听器
     */
    public interface OnDatePickedListener {
        void onDatePicked(String date);
    }

    /**
     * 日期多选监听器
     */
    public interface OnDateSelectedListener {
        void onDateSelected(List<String> date);
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mTManager = DPTManager.getInstance();
        mLManager = DPLManager.getInstance();

        // 设置排列方向为竖向
        setOrientation(VERTICAL);

        LayoutParams llParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // 标题栏根布局
        RelativeLayout rlTitle = new RelativeLayout(context);
        rlTitle.setBackgroundColor(Color.parseColor("#E8374A"));//add by andye
        //rlTitle.setBackgroundColor(mTManager.colorTitleBG());
        int rlTitlePadding = MeasureUtil.dp2px(context, 10);
        rlTitle.setPadding(rlTitlePadding, rlTitlePadding, rlTitlePadding, rlTitlePadding);

        // 周视图根布局
        LinearLayout llWeek = new LinearLayout(context);
        llWeek.setBackgroundColor(mTManager.colorTitleBG());
        llWeek.setOrientation(HORIZONTAL);
        int llWeekPadding = MeasureUtil.dp2px(context, 5);
        llWeek.setPadding(0, llWeekPadding, 0, llWeekPadding);
        LayoutParams lpWeek = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;

        // 标题栏子元素布局参数
        RelativeLayout.LayoutParams lpYear = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpYear.addRule(RelativeLayout.CENTER_VERTICAL);
//        lpYear.setMargins(Utils.dip2px(context, 50), 0, 0, 0);

        RelativeLayout.LayoutParams lpMonth =  new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpMonth.addRule(RelativeLayout.CENTER_IN_PARENT);


        RelativeLayout.LayoutParams lpEnsure =
                new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpEnsure.addRule(RelativeLayout.CENTER_VERTICAL);
        lpEnsure.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        // --------------------------------------------------------------------------------标题栏
        // 年份显示
        tvYear = new TextView(context);
        tvYear.setText("2015");
        tvYear.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvYear.setTextColor(mTManager.colorTitle());

        // 月份显示
        tvMonth = new TextView(context);
        tvMonth.setText("六月");
        tvMonth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tvMonth.setTextColor(mTManager.colorTitle());

        // 确定显示
        tvEnsure = new TextView(context);
        tvEnsure.setText(mLManager.titleEnsure());
        tvEnsure.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvEnsure.setTextColor(mTManager.colorTitle());
        tvEnsure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onDateSelectedListener) {
                    onDateSelectedListener.onDateSelected(monthView.getDateSelected());
                }
            }
        });

        // 提醒 显示
        ImageView   iv_album = new ImageView(context);
        iv_album.setImageResource(R.drawable.user_alarm);
        iv_album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent().setClass(context, AlarmListActivity.class));

            }
        });
        RelativeLayout.LayoutParams lpAlbum = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpAlbum.addRule(RelativeLayout.CENTER_VERTICAL);
        lpAlbum.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpAlbum.width = Utils.dip2px(context,25);
        lpAlbum.height = Utils.dip2px(context,25);

        // 加好友 显示
        ImageView   iv_add_fri = new ImageView(context);
        iv_add_fri.setImageResource(R.drawable.user_plus);
        iv_add_fri.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent().setClass(context, MainPlusAffairActivity.class));
            }
        });
        RelativeLayout.LayoutParams lp_add_fri = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp_add_fri.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_add_fri.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_add_fri.setMargins(0, 0, Utils.dip2px(context, 50), 0);
        lp_add_fri.addRule(RelativeLayout.LEFT_OF);
        lp_add_fri.width = Utils.dip2px(context,25);
        lp_add_fri.height = Utils.dip2px(context,25);

        rlTitle.addView(tvYear, lpYear);
        rlTitle.addView(tvMonth, lpMonth);
        rlTitle.addView(tvEnsure, lpEnsure);
        rlTitle.addView(iv_album, lpAlbum);
        rlTitle.addView(iv_add_fri, lp_add_fri);


        addView(rlTitle, llParams);




        // --------------------------------------------------------------------------------周视图
        for (int i = 0; i < mLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(context);
            tvWeek.setText(mLManager.titleWeek()[i]);
            tvWeek.setGravity(Gravity.CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvWeek.setTextColor(Color.parseColor("#000000"));//andye
            llWeek.setBackgroundColor(Color.parseColor("#ffffff"));//andye
//            tvWeek.setTextColor(mTManager.colorTitle());
            llWeek.addView(tvWeek, lpWeek);
        }
        addView(llWeek, llParams);

        // ------------------------------------------------------------------------------------月视图
        monthView = new MonthView(context);
        monthView.setOnDateChangeListener(new MonthView.OnDateChangeListener() {
            @Override
            public void onMonthChange(int month) {
                tvMonth.setText(mLManager.titleMonth()[month - 1]);

            }

            @Override
            public void onYearChange(int year) {
                String tmp = String.valueOf(year);
                if (tmp.startsWith("-")) {
                    tmp = tmp.replace("-", mLManager.titleBC());
                }
                tvYear.setText(tmp);
            }
        });
        addView(monthView, llParams);
    }

    /**
     * 设置初始化年月日期
     *
     * @param year  ...
     * @param month ...
     */
    public void setDate(int year, int month) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        monthView.setDate(year, month);
    }

    public void setDPDecor(DPDecor decor) {
        monthView.setDPDecor(decor);
    }

    /**
     * 设置日期选择模式
     *
     * @param mode ...
     */
    public void setMode(DPMode mode) {
        if (mode != DPMode.MULTIPLE) {
            tvEnsure.setVisibility(GONE);
        }
        monthView.setDPMode(mode);
    }

    public void setFestivalDisplay(boolean isFestivalDisplay) {
        monthView.setFestivalDisplay(isFestivalDisplay);
    }

    public void setTodayDisplay(boolean isTodayDisplay) {
        monthView.setTodayDisplay(isTodayDisplay);
    }

    public void setHolidayDisplay(boolean isHolidayDisplay) {
        monthView.setHolidayDisplay(isHolidayDisplay);
    }

    public void setDeferredDisplay(boolean isDeferredDisplay) {
        monthView.setDeferredDisplay(isDeferredDisplay);
    }

    /**
     * 设置单选监听器
     *
     * @param onDatePickedListener ...
     */
    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
        if (monthView.getDPMode() != DPMode.SINGLE) {
            throw new RuntimeException(
                    "Current DPMode does not SINGLE! Please call setMode set DPMode to SINGLE!");
        }
        monthView.setOnDatePickedListener(onDatePickedListener);
    }

    /**
     * 设置多选监听器
     *
     * @param onDateSelectedListener ...
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        if (monthView.getDPMode() != DPMode.MULTIPLE) {
            throw new RuntimeException(
                    "Current DPMode does not MULTIPLE! Please call setMode set DPMode to MULTIPLE!");
        }
        this.onDateSelectedListener = onDateSelectedListener;
    }
}
