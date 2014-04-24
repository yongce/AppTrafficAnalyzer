package me.ycdev.android.trafficanalyzer.stats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;
import me.ycdev.androidlib.utils.DateTimeUtils;
import me.ycdev.androidlib.utils.IoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Process;
import android.os.SystemClock;

import eu.chainfire.libsuperuser.Shell;

public class SnapshotsMgr {
    private static final String TAG = "SnapshotsMgr";
    private static final boolean DEBUG = AppLogger.DEBUG;

    private static final String STATS_FILE = "/proc/net/xt_qtaguid/stats";

    private static final String SNAPSHOTS_DIR = "snapshots";

    private static final String META_FILENAME = "snapshots_meta";
    private static final String KEY_CREATE_TIME = "create_time";
    private static final String KEY_CLOCK_TIME = "clock_time";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_NOTES = "notes";

    private Context mAppContext;
    private File mSnapsDir;
    private List<SnapshotItem> mSnapshotsList = new ArrayList<SnapshotItem>();
    private boolean mMetaLoaded = false;

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
        if (mMetaLoaded) {
            return;
        }

        File metaFile = new File(mSnapsDir, META_FILENAME);
        if (!metaFile.exists()) {
            return;
        }

        try {
            String metaInfo = IoUtils.readAllLines(metaFile.getAbsolutePath());
            JSONArray rootJson = new JSONArray(metaInfo);
            final int N = rootJson.length();
            for (int i = 0; i < N; i++) {
                JSONObject itemJson = rootJson.getJSONObject(i);
                SnapshotItem item = new SnapshotItem();
                item.createTime = itemJson.getLong(KEY_CREATE_TIME);
                item.clockTime = itemJson.getLong(KEY_CLOCK_TIME);
                item.fileName = itemJson.getString(KEY_FILE_NAME);
                item.notes = itemJson.getString(KEY_NOTES);
                mSnapshotsList.add(item);
            }
            mMetaLoaded = true;
        } catch (IOException e) {
            AppLogger.w(TAG, "failed to load snapshots meta info", e);
        } catch (JSONException e) {
            AppLogger.w(TAG, "failed to load snapshots meta info", e);
        }
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
            File metaFile = new File(mSnapsDir, META_FILENAME);
            IoUtils.saveAsFile(rootJson.toString(), metaFile.getAbsolutePath());
        } catch (JSONException e) {
            AppLogger.w(TAG, "failed to save snapshots meta info", e);
        } catch (IOException e) {
            AppLogger.w(TAG, "failed to save snapshots meta info", e);
        }
    }

    public synchronized boolean createSnapshot(String notes) {
        SnapshotItem item = new SnapshotItem();
        item.createTime = System.currentTimeMillis();
        item.clockTime = SystemClock.elapsedRealtime();
        item.fileName = DateTimeUtils.generateFileName(item.createTime);
        item.notes = notes;

        File snapFile = new File(mSnapsDir, item.fileName);
        int myUid = Process.myUid();
        String[] cmds = new String[] {
                "cat " + STATS_FILE + " > " + snapFile.getAbsolutePath(),
                "chown " + myUid + ":" + myUid + " " + snapFile.getAbsolutePath()
        };
        Shell.SU.run(cmds);

        loadMetaInfo();
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

    /**
     * Get all traffic stats snapshots.
     * @return Never be null.
     */
    public synchronized List<SnapshotItem> getAllSnapshots() {
        loadMetaInfo();
        // Just do a shadow clone
        return new ArrayList<SnapshotItem>(mSnapshotsList);
    }
}
