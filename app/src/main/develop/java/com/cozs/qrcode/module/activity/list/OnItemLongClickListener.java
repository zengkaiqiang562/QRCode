package com.cozs.qrcode.module.activity.list;

/**
 * 监听 RecyclerView item 的长按
 */
public interface OnItemLongClickListener<T>  {
    void onItemLongClick(int item, T object);

}
