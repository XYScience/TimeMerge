package com.science.timemerge;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/12/23
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    private List<String> mListTimes;

    public MyAdapter(List<String> listTimes) {
        mListTimes = listTimes;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.time.setText(mListTimes.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder, mListTimes.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListTimes.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView time;

        public MyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            time = (TextView) itemView.findViewById(R.id.tv_time_period);
        }
    }

    public List<String> getDatas() {
        return mListTimes;
    }

    public void setNewDatas(List<String> list) {
        mListTimes.clear();
        mListTimes.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(MyHolder holder) {
        mListTimes.remove(holder.getLayoutPosition());
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onItemClick(MyHolder holder, String time, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
