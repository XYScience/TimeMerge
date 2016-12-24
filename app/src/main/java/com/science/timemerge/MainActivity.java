package com.science.timemerge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private MyAdapter mAdapter;
    private TextView mTvNewTimePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvNewTimePeriod = (TextView) findViewById(R.id.tv_new_time);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] times = {"06:00-12:00"};
        // List<String> listTime = Arrays.asList(times);不支持remove等方法
        List<String> listTime = new ArrayList<>(Arrays.asList(times));
        mAdapter = new MyAdapter(listTime);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyAdapter.MyHolder holder, String time, int position) {
                getTime(holder, position, time, mAdapter.getDatas().size() == 1 ? "取消" : "删除");
            }
        });
    }

    public void onClick(View view) {
        getTime(null, -1, mAdapter.getDatas().get(mAdapter.getItemCount() - 1), "取消");
    }

    /**
     * 检查时间格式，并打开弹窗选择时间。
     *
     * @param holder
     * @param position
     * @param time
     * @param negative
     */
    private void getTime(final MyAdapter.MyHolder holder, final int position, final String time, String negative) {
        String isTimePeriod = "^([0-1][0-9]|[2][0-3]):([0-5][0-9])-([0-1][0-9]|[2][0-4]):([0-5][0-9])$";
        if (!Pattern.matches(isTimePeriod, time)) {
            Toast.makeText(this, "时间格式错误!", Toast.LENGTH_SHORT).show();
            return;
        }
        String tt[] = time.split("-");
        String t = "24:00".equals(tt[1]) ? tt[0] + "-00:00" : time;
        PeriodTimeDialog dialog = new PeriodTimeDialog(this, "营业时间", negative);
        dialog.initTime(t);
        dialog.setCloseListener(new PeriodTimeDialog.DialogCloseListener() {
            @Override
            public void confirm(String timeData) {
                updateTime(position, timeData);
            }

            @Override
            public void delete() {
                mAdapter.remove(holder);
                mTvNewTimePeriod.setVisibility(mAdapter.getDatas().size() < 3 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 合并更新时间
     *
     * @param position 列表为1个，且position为0，则直接更新item内容
     * @param timeData
     */
    private void updateTime(int position, String timeData) {
        String timesCurrent[] = timeData.split("-");
        if ("00:00".equals(timesCurrent[1])) {
            timesCurrent[1] = "24:00";
        }
        // 开始时间大于或等于结束时间则认为是跨天。如果00:00-00:00(相当于00:00-24:00)则认为一整天
        if (compare(timesCurrent[0], timesCurrent[1]) == -1 || compare(timesCurrent[0], timesCurrent[1]) == 0) {
            Toast.makeText(this, "营业时间不能跨天!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.getItemCount() == 1 && position == 0) {
            mAdapter.updateData(timesCurrent[0] + "-" + timesCurrent[1]);
        } else {
            // 修改or新建营业时间
            List<BusinessTime> beforeTimeList = new ArrayList<>();
            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                String times[] = mAdapter.getDatas().get(i).split("-");
                beforeTimeList.add(new BusinessTime(times[0], times[1]));
            }
            beforeTimeList.add(new BusinessTime(timesCurrent[0], timesCurrent[1]));
            List<BusinessTime> timeList = mergeTime(beforeTimeList);
            List<String> list = new ArrayList<>();
            for (BusinessTime time : timeList) {
                list.add(time.getStart() + "-" + time.getEnd());
            }
            mAdapter.setNewDatas(list);
            mTvNewTimePeriod.setVisibility(mAdapter.getDatas().size() == 3 ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 时间段合并排序
     *
     * @param businessTimes
     * @return
     */
    private List<BusinessTime> mergeTime(List<BusinessTime> businessTimes) {
        Collections.sort(businessTimes);

        Stack s = new Stack();
        Stack e = new Stack();
        s.push("000:00");
        e.push("000:00");
        Log.e(">>>>>", "↓要合并的时间段↓");
        for (BusinessTime time : businessTimes) {
            Log.e(">>>>>", time.getStart() + " " + time.getEnd());
            if (compare(time.getStart(), time.getEnd()) == -1)
                try {
                    throw new Exception("The time is incorrect.");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            // 首先最小的时间段进栈，如果次小时间段的开始时间>最小时间段的结束时间，则说明次小时间段和最小时间段没有交集
            // time.getStart()>e.peek()
            if (compare(time.getStart(), (String) e.peek()) == -1) {
                s.push(time.getStart());
                e.push(time.getEnd());
            }
            // time.getEnd()>e.peek()
            // 假如首先次小时间段的开始时间<最小时间段的结束时间(即上面的if语句块不执行)，
            // 即次小时间段的开始时间位于最小时间段里(因为时间段是从小到大排序的，次小时间段开始时间已经大于最小时间段的开始时间)，
            // (1)，如果次小时间段的结束时间>最小时间段的结束时间，则说明有交集、且合并时间段的结束时间是次小时间段的结束时间；
            else if (compare(time.getEnd(), (String) e.peek()) == -1) {
                e.pop();
                e.push(time.getEnd());
            }
            // (2)，如果次小时间段的结束时间<=最小时间段的结束时间，则说明最小时间段包含次小时间段。
            //else {}
        }

        businessTimes.clear();
        while (!s.empty()) {
            businessTimes.add(new BusinessTime((String) s.peek(), (String) e.peek()));
            e.pop();
            s.pop();
        }
        Collections.sort(businessTimes);
        businessTimes.remove(0);
        Log.e(">>>>>", "↓已经合并的时间段↓");
        for (BusinessTime time : businessTimes) {
            Log.e(">>>>>", time.getStart() + " " + time.getEnd());
        }
        return businessTimes;
    }

    class BusinessTime implements Comparable<BusinessTime> {

        private String start;
        private String end;

        BusinessTime(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public int compareTo(BusinessTime other) {
            if (compare(start, other.start) == 0) {
                return compare(other.end, end);
            }
            return compare(other.start, start);
        }
    }

    public static int compare(String start, String end) {
        String starts[] = start.split(":");
        String ends[] = end.split(":");
        if (Integer.valueOf(starts[0]) < Integer.valueOf(ends[0])) {
            return 1;
        } else if (Integer.valueOf(starts[0]) > Integer.valueOf(ends[0])) {
            return -1;
        } else if (starts[0].equals(ends[0])) {
            if (Integer.valueOf(starts[1]) < Integer.valueOf(ends[1])) {
                return 1;
            } else if (Integer.valueOf(starts[1]) > Integer.valueOf(ends[1])) {
                return -1;
            } else {
                return 0;
            }
        }
        return -1;
    }
}
