package com.cozs.qrcode.module.zxing.ext.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void addHistory(HistoryEntity history);


    @Delete
    void deleteHistory(HistoryEntity... histories);

    @Query("delete from result_history where rawText= :rawText")
    void deleteRepeatHistory(String rawText); // 删除重复历史记录


    @Update
    void updateHistory(HistoryEntity history); // 是否收藏用到这个


    @Query("select * from result_history order by createTime desc")
    List<HistoryEntity> queryAllHistory();

    @Query("select * from result_history where favorite=1 order by createTime desc")
    List<HistoryEntity> queryFavoriteHistory();
}
