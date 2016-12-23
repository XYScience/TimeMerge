package com.science.timemerge;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/12/23
 */

public class PeriodTimeDialog {

    private AlertDialog.Builder mBuilder;
    private String start_hour = "00"; // 开始营业的时
    private String start_minute = "00"; // 开始营业的分
    private String end_hour = "00"; // 停止营业的时
    private String end_minute = "00";  // 停止营业的分
    private String timeData = "00:00-00:00"; // 默认的营业时间段返回值
    private PickerView mPvStartHour;
    private PickerView mPvStartMinute;
    private PickerView mPvEndHour;
    private PickerView mPvEndMinute;
    private DialogCloseListener mCloseListener;

    public PeriodTimeDialog(final Context context, String title, String text) {
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(title);
        View view = LayoutInflater.from(context).inflate(R.layout.period_time, null);
        mBuilder.setView(view);
        initView(view);
        mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 选择时间后返回的数据
                timeData = start_hour + ":" + start_minute + "-" + end_hour + ":" + end_minute;
                if (mCloseListener != null) {
                    mCloseListener.confirm(timeData);
                }
            }
        });
        if ("取消".equals(text)) {
            mBuilder.setNegativeButton(text, null);
        } else {
            mBuilder.setNegativeButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mCloseListener.delete();
                }
            });
        }
        mBuilder.show();
    }

    private void initView(View view) {
        mPvStartHour = (PickerView) view.findViewById(R.id.pv_start_hour);
        mPvStartMinute = (PickerView) view.findViewById(R.id.pv_start_minute);
        mPvEndHour = (PickerView) view.findViewById(R.id.pv_end_hour);
        mPvEndMinute = (PickerView) view.findViewById(R.id.pv_end_minute);

        initDate();
        initListener();
    }

    /**
     * 设置对话框时间的小时和分钟数据格式(12小时制还是24小时制)
     */
    private void initDate() {
        List<String> data = new ArrayList<>();
        List<String> seconds = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            data.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        // 设置时间对话框的时间数据
        mPvStartHour.setData(data);
        mPvStartMinute.setData(seconds);
        mPvEndHour.setData(data);
        mPvEndMinute.setData(seconds);
    }

    private void initListener() {
        mPvStartHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_hour = text;
            }
        });
        mPvStartMinute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_minute = text;
            }
        });
        mPvEndHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_hour = text;
            }
        });
        mPvEndMinute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_minute = text;
            }
        });
    }

    /**
     * 初始化对话框的选中显示时间
     *
     * @param time 外部传入的开始时间
     */
    public void initTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            // 时间段的切割
            StringTokenizer st = new StringTokenizer(time, ":-");

            while (st.hasMoreElements()) {
                // 把切割的字符串数据赋值给时间段的四个值，达到对话框没有滑动更改时间的时候返回去的数据是之前显示的数据
                start_hour = st.nextToken().trim();
                start_minute = st.nextToken().trim();
                end_hour = st.nextToken().trim();
                end_minute = st.nextToken().trim();
            }
        }

        // 根据切割的字符串数值指定打开时间对话框时默认选定的时间是之前显示的时间
        mPvStartHour.setSelected(Integer.valueOf(start_hour));
        mPvStartMinute.setSelected(Integer.valueOf(start_minute));
        mPvEndHour.setSelected(Integer.valueOf(end_hour));
        mPvEndMinute.setSelected(Integer.valueOf(end_minute));

    }

    public interface DialogCloseListener {
        void confirm(String timeData);

        void delete();
    }

    // 回调接口
    public void setCloseListener(DialogCloseListener listener) {
        mCloseListener = listener;
    }

}
