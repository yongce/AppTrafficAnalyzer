package me.ycdev.android.trafficanalyzer.stats;

public class SnapshotItem implements Comparable<SnapshotItem> {
    public long createTime;
    public long clockTime;
    public String fileName;
    public String notes;

    @Override
    public int compareTo(SnapshotItem another) {
        if (createTime > another.createTime) {
            return 1;
        } else if (createTime < another.createTime) {
            return -1;
        }
        return 0;
    }
}
