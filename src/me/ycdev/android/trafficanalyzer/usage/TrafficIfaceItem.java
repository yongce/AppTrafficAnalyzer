package me.ycdev.android.trafficanalyzer.usage;

public class TrafficIfaceItem {
    public String iface;
    public boolean checked;

    public TrafficIfaceItem(String iface, boolean checked) {
        this.iface = iface;
        this.checked = checked;
    }
}
