package me.ycdev.android.trafficanalyzer.usage;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.androidlib.ui.base.ListAdapterBase;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TrafficUsageAdapter extends ListAdapterBase<TrafficUsageItem> {
    public TrafficUsageAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    protected int getItemResId() {
        return R.layout.usage_traffic_item;
    }

    @Override
    protected ViewHolderBase createViewHolder(View itemView, int position) {
        return new ViewHolder(itemView, position);
    }

    @Override
    protected void bindView(TrafficUsageItem item, ViewHolderBase holder) {
        ViewHolder viewHolder = (ViewHolder) holder;

        if (item.isTotal) {
            viewHolder.ifaceView.setText("*");
            viewHolder.tagView.setText("*");
            viewHolder.fgView.setText("*");
        } else {
            viewHolder.ifaceView.setText(item.usage.iface);
            viewHolder.tagView.setText("0x" + Integer.toHexString(item.usage.tag));
            viewHolder.fgView.setText(item.usage.foreground ? "Y" : "N");
        }
        viewHolder.sendView.setText(String.valueOf(item.usage.sendBytes));
        viewHolder.recvView.setText(String.valueOf(item.usage.recvBytes));
        viewHolder.totalView.setText(String.valueOf(item.usage.sendBytes + item.usage.recvBytes));
    }

    public static void addHeaderView(LayoutInflater inflater, ListView listView) {
        View itemView = inflater.inflate(R.layout.usage_traffic_item, listView, false);
        ViewHolder holder = new ViewHolder(itemView, 0);
        holder.ifaceView.setText(R.string.usage_traffic_column_iface);
        holder.tagView.setText(R.string.usage_traffic_column_tag);
        holder.fgView.setText(R.string.usage_traffic_column_fg);
        holder.sendView.setText(R.string.usage_traffic_column_send);
        holder.recvView.setText(R.string.usage_traffic_column_recv);
        holder.totalView.setText(R.string.usage_traffic_column_total);
        listView.addHeaderView(itemView);
    }

    private static class ViewHolder extends ViewHolderBase {
        TextView ifaceView;
        TextView tagView;
        TextView fgView;
        TextView sendView;
        TextView recvView;
        TextView totalView;

        ViewHolder(View itemView, int position) {
            super(itemView, position);
        }

        @Override
        protected void findViews() {
            ifaceView = (TextView) itemView.findViewById(R.id.iface);
            tagView = (TextView) itemView.findViewById(R.id.tag);
            fgView = (TextView) itemView.findViewById(R.id.fg);
            sendView = (TextView) itemView.findViewById(R.id.send);
            recvView = (TextView) itemView.findViewById(R.id.recv);
            totalView = (TextView) itemView.findViewById(R.id.total);
        }
    }
}
