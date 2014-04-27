package me.ycdev.android.trafficanalyzer;

import java.util.Collections;
import java.util.List;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.android.trafficanalyzer.stats.StatsSnapshot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SnapshotsAdapter extends BaseAdapter {
    private Context mContext;
    private ListView mListView;
    private LayoutInflater mInflater;
    private List<StatsSnapshot> mItems;

    public SnapshotsAdapter(Context cxt, ListView listView) {
        mContext = cxt;
        mListView = listView;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<StatsSnapshot> data) {
        mItems = data;
        Collections.sort(mItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public StatsSnapshot getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mInflater, convertView, parent);
        StatsSnapshot item = getItem(position);

        holder.checkBox.setChecked(mListView.isItemChecked(position));
        holder.labelView.setText(mContext.getString(R.string.snapshot_label, item.fileName));
        holder.notesView.setText(mContext.getString(R.string.snapshot_notes, item.notes));

        return holder.rootView;
    }

}

class ViewHolder {
    View rootView;
    CheckBox checkBox;
    TextView labelView;
    TextView notesView;

    public ViewHolder(View convertView) {
        rootView = convertView;
        checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        labelView = (TextView) convertView.findViewById(R.id.label);
        notesView = (TextView) convertView.findViewById(R.id.notes);
    }

    public static ViewHolder get(LayoutInflater inflater, View convertView,
            ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.snapshots_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return holder;
    }
}
