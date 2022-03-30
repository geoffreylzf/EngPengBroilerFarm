package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.model.FeedDischargeLocation;

public class TempFeedDischargeHeadSelectionAdapter extends RecyclerView.Adapter<TempFeedDischargeHeadSelectionAdapter.ItemViewHolder> {
    final private Context context;
    private List<FeedDischargeLocation> feedDischargeLocationList;

    public TempFeedDischargeHeadSelectionAdapter(Context context, List<FeedDischargeLocation> feedDischargeLocationList) {
        this.context = context;
        this.feedDischargeLocationList = feedDischargeLocationList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_feed_discharge_head_selection, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final FeedDischargeLocation fdl = feedDischargeLocationList.get(position);
        holder.tvLocationCode.setText(fdl.getLocationCode());
        holder.tvLocationName.setText(fdl.getCompanyCode() + " - " + fdl.getLocationName());
        holder.view.setBackgroundColor(fdl.isSelect() ?
                context.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(fdl.isSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fdl.setSelect(!fdl.isSelect());

                TempFeedDischargeHeadSelectionAdapter.this.notifyDataSetChanged();
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fdl.setSelect(!fdl.isSelect());

                TempFeedDischargeHeadSelectionAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public int getItemCount() {
        return feedDischargeLocationList == null ? 0 : feedDischargeLocationList.size();
    }

    private void resetFeedItemList() {
        for (int i = 0; i < feedDischargeLocationList.size(); i++) {
            FeedDischargeLocation fdl = feedDischargeLocationList.get(i);
            fdl.setSelect(false);
        }
    }

    public void setList(List<FeedDischargeLocation> newList) {
        this.feedDischargeLocationList = newList;
        this.notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvLocationName, tvLocationCode;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvLocationName = itemView.findViewById(R.id.li_temp_feed_discharge_head_selection_tv_location_name);
            tvLocationCode = itemView.findViewById(R.id.li_temp_feed_discharge_head_selection_tv_location_code);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_discharge_head_selection_cb);
        }
    }
}
