package me.ycdev.android.trafficanalyzer.stats;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;

import android.text.TextUtils;

public class TagTrafficStats implements Cloneable {
    private static final String TAG = "TagTrafficStats";

    public static class Type implements Comparable<Type> {
        private TagTrafficStats mTarget;

        public Type(TagTrafficStats target) {
            mTarget = target;
        }

        @Override
        public int compareTo(Type another) {
            int result = mTarget.iface.compareTo(another.mTarget.iface);
            if (result == 0) {
                result = mTarget.tag - another.mTarget.tag;
                if (result == 0) {
                    if (mTarget.foreground && !another.mTarget.foreground) {
                        result = 1;
                    } else if (!mTarget.foreground && another.mTarget.foreground) {
                        result = -1;
                    } else {
                        result = 0;
                    }
                }
            }
            return result;
        }
    }

    public String iface;
    public int tag;
    public boolean foreground;
    public long sendBytes;
    public long recvBytes;

    /**
     * @param oldStats Can be null
     */
    public TagTrafficStats subtract(TagTrafficStats oldStats) {
        if (oldStats == null) {
            return clone();
        }

        if (!hasSameType(oldStats)) {
            throw new IllegalArgumentException("not same type");
        }
        if (sendBytes < oldStats.sendBytes || recvBytes < oldStats.recvBytes) {
            throw new IllegalArgumentException("negative usage");
        }

        TagTrafficStats usage = clone();
        usage.sendBytes -= oldStats.sendBytes;
        usage.recvBytes -= oldStats.recvBytes;
        return usage;
    }

    @Override
    public TagTrafficStats clone() {
        try {
            return (TagTrafficStats) super.clone();
        } catch (CloneNotSupportedException e) {
            // never happen
            AppLogger.w(TAG, "what's up", e);
        }
        return null; // never reach here
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TagTrafficStats[");
        sb.append("iface=").append(iface);
        sb.append(", tag=").append(tag);
        sb.append(", foreground=").append(foreground);
        sb.append(", sendBytes=").append(sendBytes);
        sb.append(", recvBytes=").append(recvBytes);
        sb.append("]");
        return sb.toString();
    }

    private boolean hasSameType(TagTrafficStats rhs) {
        return tag == rhs.tag && foreground == rhs.foreground
                && TextUtils.equals(iface, rhs.iface);
    }
}
