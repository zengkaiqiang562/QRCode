package com.cozs.qrcode.module.activity.list;

/**
 * 监听 RecyclerView item 的点击
 */
public interface OnItemClickListener<T>  {
    void onItemClick(int item, T object);
}
