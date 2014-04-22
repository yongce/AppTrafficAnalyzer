package me.ycdev.android.trafficanalyzer.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateFormat;

public class SnapshotsMgr {
    private static final String TAG = "SnapshotsMgr";
    private static final boolean DEBUG = AppLogger.DEBUG;

    private static final String SNAPSHOTS_DIR = "snapshots";

    private static final String META_FILENAME = "meta";
    private static final String KEY_CREATE_TIME = "create_time";
    private static final String KEY_CLOCK_TIME = "clock_time";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_NOTES = "notes";

    private Context mAppContext;
    private File mSnapsDir;
    private List<SnapshotItem> mSnapshotsList;

    private static volatile SnapshotsMgr sInstance;

    private SnapshotsMgr(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mSnapsDir = mAppContext.getDir(SNAPSHOTS_DIR, Context.MODE_PRIVATE);
        loadMetaInfo();
    }

    public static SnapshotsMgr getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (SnapshotsMgr.class) {
                if (sInstance == null) {
                    sInstance = new SnapshotsMgr(cxt);
                }
            }
        }
        return sInstance;
    }

    private void loadMetaInfo() {
        File metaFile = new File(mSnapsDir, META_FILENAME);
        if (!metaFile.exists()) {
            mSnapshotsList = new ArrayList<SnapshotItem>();
            return;
        }

        // TODO load the meta file content
    }

    private void saveMetaInfo() {
        try {
            JSONArray rootJson = new JSONArray();
            for (SnapshotItem item : mSnapshotsList) {
                JSONObject itemJson = new JSONObject();
                itemJson.put(KEY_CREATE_TIME, item.createTime);
                itemJson.put(KEY_CLOCK_TIME, item.clockTime);
                itemJson.put(KEY_FILE_NAME, item.fileName);
                itemJson.put(KEY_NOTES, item.notes);
                rootJson.put(itemJson);
            }
            // TODO write to the meta file
        } catch (JSONException e) {
            AppLogger.w(TAG, "failed to save meta", e);
        }
    }

    public synchronized boolean createSnapshot(String notes) {
        SnapshotItem item = new SnapshotItem();
        item.createTime = System.currentTimeMillis();
        item.clockTime = SystemClock.elapsedRealtime();
        item.fileName = DateFormat.format("yyyyMMdd-HHmmss-SSS", item.createTime).toString();
        item.notes = notes;

        File snapFile = new File(mSnapsDir, item.fileName);
        // TODO create the snapshot file

        mSnapshotsList.add(item);
        saveMetaInfo();

        return true;
    }

    public synchronized void clearAllSnapshots() {
        for (SnapshotItem item : mSnapshotsList) {
            new File(mSnapsDir, item.fileName).delete();
        }
        mSnapshotsList.clear();
        saveMetaInfo();
    }

    public synchronized List<SnapshotItem> getAllSnapshots() {
        // Just do a shadow clone
        return new ArrayList<SnapshotItem>(mSnapshotsList);
    }
}
