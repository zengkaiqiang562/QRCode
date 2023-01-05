package com.cozs.qrcode.module.activity.list;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础的Adapter
 * M : 用于该 Adapter 的列表的数据类型,即 List<M> .
 * H : 即和 Adapter 绑定的 Holder 的类型.
 */
public abstract class BaseAdapter<M, H extends BaseHolder<M>> extends RecyclerView.Adapter<H> {

    public List<M> getDataList() {
        return dataList;
    }

    public void setDataList(List<M> dataList) {
        this.dataList = dataList;
    }

    protected List<M> dataList;
    protected Context context;
    protected OnItemClickListener<H> listener;
    protected OnItemLongClickListener<H> longListener;

    /**
     * 设置数据,并设置点击回调接口
     *
     * @param list     数据集合
     * @param listener 回调接口
     */
    public BaseAdapter(@Nullable List<M> list, OnItemClickListener<H> listener, Context context) {
        this.dataList = list;
        this.context = context;
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }

        this.listener = listener;
    }

    /**
     * 设置数据,并设置点击回调接口
     *
     * @param list         数据集合
     * @param listener     回调接口
     * @param longListener 回调接口
     */
    public BaseAdapter(List<M> list, OnItemClickListener<H> listener, OnItemLongClickListener<H> longListener, Context context) {
        this(list, listener, context);
        this.longListener = longListener;
    }

    @Override
    public void onBindViewHolder(final H holder, final int position) {
        if (dataList != null && dataList.size() > 0 && dataList.size() >= position + 1)
            holder.setData(dataList.get(position), position);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> {
                listener.onItemClick(position, holder);
            });
        }

        if (longListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                longListener.onItemLongClick(position, holder);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public Context getContext(){
        return context;
    }

    public void clearDatas() {
        if (dataList != null) dataList.clear();
    }

    /**
     * 填充数据,此方法会清空以前的数据
     *
     * @param list 需要显示的数据
     */
    public void fillDatas(List<M> list) {
        this.dataList = list;
        notifyDataSetChanged();

    }

    /**
     * 获取一条数据
     *
     * @param holder item对应的holder
     * @return 该item对应的数据
     */
    public M getItem(H holder) {
        return dataList.get(holder.getLayoutPosition());
    }

    /**
     * 获取一条数据
     *
     * @param position item的位置
     * @return item对应的数据
     */
    public M getItem(int position) {
        return dataList.get(position);
    }
}