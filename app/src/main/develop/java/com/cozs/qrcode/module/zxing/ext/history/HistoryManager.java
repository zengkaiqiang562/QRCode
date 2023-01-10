package com.cozs.qrcode.module.zxing.ext.history;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.cozs.qrcode.module.QRApplication;
import com.cozs.qrcode.module.zxing.Preferences;

import java.util.List;

public class HistoryManager {
    private volatile static HistoryManager instance;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
        /*
         * 历史记录 db 操作的 Handler 线程
         */
        HandlerThread historyThread = new HandlerThread("thread_history");
        historyThread.start();
        historyHandler = new HistoryHandler(historyThread.getLooper());
        HistoryDatabase historyDB = HistoryDatabase.getInstance();
        historyDao = historyDB.historyDao();
        trimHistory();
    }

    public void addHistory(HistoryEntity history) {
        historyHandler.post(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(QRApplication.getContext());
            if (!prefs.getBoolean(Preferences.KEY_REMEMBER_DUPLICATES, false)) {
                // 不保存重复记录时，先删除之前重复的，再添加
                deleteRepeatHistory(history.rawText);
            }
            historyDao.addHistory(history);
        });
    }

    public void deleteHistory(HistoryEntity history) {
        historyHandler.post(() -> {
            historyDao.deleteHistory(history);
        });
    }

    public void deleteHistory(List<HistoryEntity> histories) {
        historyHandler.post(() -> {
            historyDao.deleteHistory(histories);
        });
    }

    private void deleteRepeatHistory(String rawText) {
        historyHandler.post(() -> {
            historyDao.deleteRepeatHistory(rawText);
        });
    }

    public void updateHistory(HistoryEntity history) {
        historyHandler.post(() -> {
            historyDao.updateHistory(history);
        });
    }

    public void queryAll(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<HistoryEntity> histories = historyDao.queryAll();
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(histories);
                }
            });
        });
    }

    public void queryFavorite(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<HistoryEntity> histories = historyDao.queryFavorite();
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(histories);
                }
            });
        });
    }

    // 用于判断是否存在历史记录
    public void countAll(QueryCountCallback callback) {
        historyHandler.post(() -> {
            int count = historyDao.countHistory();
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }

    // 用于判断是否存在收藏
    public void countFavorite(QueryCountCallback callback) {
        historyHandler.post(() -> {
            int count = historyDao.countFavorite();
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }

    // 最多只保存最近的1000个未收藏的，500个收藏的（即1500个历史记录）
    private void trimHistory() {
        historyHandler.post(() -> {
            List<HistoryEntity> notFavorites = historyDao.queryNotFavorite();
            if (notFavorites.size() > 1000) {
                List<HistoryEntity> expiredNotFavorites = notFavorites.subList(1000, notFavorites.size());
                historyDao.deleteHistory(expiredNotFavorites);
            }

            List<HistoryEntity> favorites = historyDao.queryFavorite();
            if (favorites.size() > 500) {
                List<HistoryEntity> expiredFavorites = favorites.subList(500, favorites.size());
                historyDao.deleteHistory(expiredFavorites);
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

    public interface QueryCountCallback {
        void onSuccess(int count);
    }
}
