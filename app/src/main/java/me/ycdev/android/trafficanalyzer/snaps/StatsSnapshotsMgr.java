package me.ycdev.android.trafficanalyzer.snaps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

public class StatsSnapshotsMgr {
    private static final String TAG = "StatsSnapshotsMgr";
    private static final boolean DEBUG = AppLogger.DEBUG;

    private static final String STATS_FILE = "/proc/net/xt_qtaguid/stats";

    private static final String SNAPSHOTS_DIR = "snapshots";

    private static final String META_FILENAME = "snapshots_meta";
    private static final String KEY_CREATE_TIME = "create_time";
    private static final String KEY_CLOCK_TIME = "clock_time";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_NOTES = "notes";

    private Context mAppContext;
    private String mSnapsDir;
    private Set<StatsSnapshot> mAllSnapshots;
    private boolean mMetaLoaded = false;

    private static volatile StatsSnapshotsMgr sInstance;

    private StatsSnapshotsMgr(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mSnapsDir = mAppContext.getDir(SNAPSHOTS_DIR, Context.MODE_PRIVATE).getAbsolutePath();
        mAllSnapshots  = new TreeSet<StatsSnapshot>(new StatsSnapshot.CreateTimeComparator());
        loadMetaInfoIfNeeded();
    }

    public static StatsSnapshotsMgr getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (StatsSnapshotsMgr.class) {
                if (sInstance == null) {
                    sInstance = new StatsSnapshotsMgr(cxt);
                }
            }
        }
        return sInstance;
    }

    private void loadMetaInfoIfNeeded() {
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
            mAllSnapshots.clear(); // for special case: an item added but loading failed
            for (int i = 0; i < N; i++) {
                JSONObject itemJson = rootJson.getJSONObject(i);
                StatsSnapshot item = new StatsSnapshot();
                item.createTime = itemJson.getLong(KEY_CREATE_TIME);
                item.clockTime = itemJson.getLong(KEY_CLOCK_TIME);
                item.dirPath = mSnapsDir;
                item.fileName = itemJson.getString(KEY_FILE_NAME);
                item.notes = itemJson.getString(KEY_NOTES);
                mAllSnapshots.add(item);
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
            for (StatsSnapshot item : mAllSnapshots) {
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
        StatsSnapshot item = new StatsSnapshot();
        item.createTime = System.currentTimeMillis();
        item.clockTime = SystemClock.elapsedRealtime();
        item.dirPath = mSnapsDir;
        item.fileName = DateTimeUtils.generateFileName(item.createTime);
        item.notes = notes;

        File snapFile = new File(mSnapsDir, item.fileName);
        int myUid = Process.myUid();
        String[] cmds = new String[] {
                "cat " + STATS_FILE + " > " + snapFile.getAbsolutePath(),
                "chown " + myUid + ":" + myUid + " " + snapFile.getAbsolutePath()
        };
        Shell.SU.run(cmds);

        loadMetaInfoIfNeeded();
        // add the item even if loading failed
        mAllSnapshots.add(item);
        saveMetaInfo();

        return true;
    }

    public synchronized void clearAllSnapshots() {
        for (StatsSnapshot item : mAllSnapshots) {
            new File(mSnapsDir, item.fileName).delete();
        }
        mAllSnapshots.clear();
        saveMetaInfo();
    }

    public synchronized void updateSnapshotNotes(StatsSnapshot item) {
        saveMetaInfo();
    }

    public synchronized void deleteSnapshots(List<StatsSnapshot> deleteItems) {
        mAllSnapshots.removeAll(deleteItems);
        saveMetaInfo();
        for (StatsSnapshot item : deleteItems) {
            File snapFile = new File(mSnapsDir, item.fileName);
            if (snapFile.exists()) {
                snapFile.delete();
            }
        }
    }

    /**
     * Get all traffic stats snapshots.
     * @return Never be null.
     */
    public synchronized List<StatsSnapshot> getAllSnapshots() {
        loadMetaInfoIfNeeded();
        // Just do a shadow clone
        return new ArrayList<StatsSnapshot>(mAllSnapshots);
    }

    public File getSnapshotFile(StatsSnapshot item) {
        return new File(mSnapsDir, item.fileName);
    }
}
