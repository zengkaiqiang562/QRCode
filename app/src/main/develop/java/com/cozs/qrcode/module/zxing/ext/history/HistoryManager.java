package com.cozs.qrcode.module.zxing.ext.history;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;

import java.util.List;

public class HistoryManager {
    private volatile static HistoryManager instance;

    private final HistoryHandler historyHandler;

    private final HistoryDao historyDao;

    public static HistoryManager getInstance() {
        if(instance == null) {
            synchronized(HistoryManager.class) {
                if(instance == null) {
                    instance = new HistoryManager();
                }
            }
        }
        return instance;
    }

    private HistoryManager() {
        /**
         * 历史记录 db 操作的 Handler 线程
         */
        HandlerThread historyThread = new HandlerThread("thread_history");
        historyThread.start();
        historyHandler = new HistoryHandler(historyThread.getLooper());
        HistoryDatabase historyDB = HistoryDatabase.getInstance();
        historyDao = historyDB.historyDao();
    }

    public void addHistory(HistoryEntity history) {
        historyHandler.post(() -> {
            historyDao.addHistory(history);
        });
    }

    public void deleteHistory(HistoryEntity... histories) {
        historyHandler.post(() -> {
            historyDao.deleteHistory(histories);
        });
    }

    public void deleteRepeatHistory(String rawText) {
        historyHandler.post(() -> {
            historyDao.deleteRepeatHistory(rawText);
        });
    }

    public void updateHistory(HistoryEntity history) {
        historyHandler.post(() -> {
            historyDao.updateHistory(history);
        });
    }

    public void queryAllHistory(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<HistoryEntity> histories = historyDao.queryAllHistory();
            if (callback != null) {
                callback.onSuccess(histories);
            }
        });
    }

    public void queryFavoriteHistory(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<HistoryEntity> histories = historyDao.queryFavoriteHistory();
            if (callback != null) {
                callback.onSuccess(histories);
            }
        });
    }

    private static class HistoryHandler extends Handler {

        public HistoryHandler(Looper looper) {
            super(looper);
        }
    }

    public interface QueryHistoryCallback {
        void onSuccess(List<HistoryEntity> histories);
    }
}
