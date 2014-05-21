package me.ycdev.android.trafficanalyzer.usage;

import java.util.List;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.android.trafficanalyzer.stats.TagTrafficStats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TrafficUsageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<TagTrafficStats> mList;

    public TrafficUsageAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public void setData(List<TagTrafficStats> data) {
        mList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public TagTrafficStats getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mInflater, convertView, parent);
        TagTrafficStats item = getItem(position);

        holder.ifaceView.setText(item.iface);
        holder.tagView.setText("0x" + Integer.toHexString(item.tag));
        holder.fgView.setText(item.foreground ? "Y" : "N");
        holder.sendView.setText(String.valueOf(item.sendBytes));
        holder.recvView.setText(String.valueOf(item.recvBytes));

        return holder.itemView;
    }

    public static void addHeaderView(LayoutInflater inflater, ListView listView) {
        ViewHolder holder = ViewHolder.get(inflater, null, listView);
        holder.ifaceView.setText(R.string.usage_traffic_column_iface);
        holder.tagView.setText(R.string.usage_traffic_column_tag);
        holder.fgView.setText(R.string.usage_traffic_column_fg);
        holder.sendView.setText(R.string.usage_traffic_column_send);
        holder.recvView.setText(R.string.usage_traffic_column_recv);
        listView.addHeaderView(holder.itemView);
    }
}

class ViewHolder {
    View itemView;
    TextView ifaceView;
    TextView tagView;
    TextView fgView;
    TextView sendView;
    TextView recvView;

    ViewHolder(View view) {
        itemView = view;
        ifaceView = (TextView) itemView.findViewById(R.id.iface);
        tagView = (TextView) itemView.findViewById(R.id.tag);
        fgView = (TextView) itemView.findViewById(R.id.fg);
        sendView = (TextView) itemView.findViewById(R.id.send);
        recvView = (TextView) itemView.findViewById(R.id.recv);
    }

    static ViewHolder get(LayoutInflater inflater, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.usage_traffic_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return holder;
    }
}
