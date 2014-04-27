package me.ycdev.android.trafficanalyzer.profile;

import android.util.SparseArray;

public class AppProfile {
    private int mAppUid;
    private SparseArray<String> mTagNames;

    public AppProfile(int appUid) {
        mAppUid = appUid;
    }

    public int getAppUid() {
        return mAppUid;
    }

    /**
     * Get tag name for specified tag.
     * @param tag
     * @return null will be returned if no tag name
     */
    public String getTagName(int tag) {
        return mTagNames.get(tag);
    }

    public void exportToFile(String filePath) {
        // TODO
    }

    public static AppProfile loadFromFile(String filePath) {
        return null; // TODO
    }
}
