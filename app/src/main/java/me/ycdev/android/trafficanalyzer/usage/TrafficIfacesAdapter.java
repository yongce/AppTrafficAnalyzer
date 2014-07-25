package me.ycdev.android.trafficanalyzer.usage;

import java.util.HashSet;
import java.util.Set;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.androidlib.ui.base.ListAdapterBase;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TrafficIfacesAdapter extends ListAdapterBase<TrafficIfaceItem>
        implements OnCheckedChangeListener {
    private OnCheckedChangeListener mChangeListener;

    public TrafficIfacesAdapter(LayoutInflater inflater, OnCheckedChangeListener listener) {
        super(inflater);
        mChangeListener = listener;
    }

    public Set<String> getSelectedIfaces() {
        HashSet<String> result = new HashSet<String>();
        for (TrafficIfaceItem item : mList) {
            if (item.checked) {
                result.add(item.iface);
            }
        }
        return result;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = ((ViewHolder) buttonView.getTag()).position;
        getItem(position).checked = isChecked;
        mChangeListener.onCheckedChanged(buttonView, isChecked);
    }

    @Override
    protected int getItemResId() {
        return R.layout.usage_iface_item;
    }

    @Override
    protected ViewHolderBase createViewHolder(View itemView, int position) {
        return new ViewHolder(itemView, position);
    }

    @Override
    protected void bindView(TrafficIfaceItem item, ViewHolderBase holder) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.ifaceView.setText(item.iface);
        viewHolder.ifaceView.setChecked(item.checked);
        viewHolder.ifaceView.setOnCheckedChangeListener(this);
    }

    private static class ViewHolder extends ViewHolderBase {
        CheckBox ifaceView;

        ViewHolder(View itemView, int position) {
            super(itemView, position);
        }

        @Override
        protected void findViews() {
            ifaceView = (CheckBox) itemView.findViewById(R.id.iface);
        }
    }
}
