package com.cozs.qrcode.module.zxing.ext.history;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cozs.qrcode.module.QRApplication;

@Database(entities = {HistoryEntity.class}, version = 1, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "history_db";

    private volatile static HistoryDatabase instance;

    public static HistoryDatabase getInstance() {
        if(instance == null) {
            synchronized(HistoryDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(QRApplication.getContext(), HistoryDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract HistoryDao historyDao();
}
