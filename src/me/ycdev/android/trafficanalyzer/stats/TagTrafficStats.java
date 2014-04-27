package me.ycdev.android.trafficanalyzer.stats;

import android.text.TextUtils;

public class TagTrafficStats {
    public String iface;
    public int tag;
    public boolean foreground;
    public long sendBytes;
    public long recvBytes;

    public TagTrafficStats subtract(TagTrafficStats oldStats) {
        if (oldStats == null) {
            return clone();
        }

        if (!equals(oldStats)) {
            throw new IllegalArgumentException("not same tag");
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
        }
        return null; // never reach here
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TagTrafficStats) {
            TagTrafficStats rhs = (TagTrafficStats) o;
            return tag == rhs.tag && foreground == rhs.foreground
                    && TextUtils.equals(iface, rhs.iface);
        }
        return false;
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

}
