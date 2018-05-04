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
import my.com.engpeng.engpeng.model.FeedItem;

public class TempFeedInItemSelectionAdapter extends RecyclerView.Adapter<TempFeedInItemSelectionAdapter.ItemViewHolder>{

    private Context mContext;
    private List<FeedItem> mFeedItemList;

    public TempFeedInItemSelectionAdapter(Context context, List<FeedItem> feedItemList){
        this.mContext = context;
        this.mFeedItemList = feedItemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_temp_feed_in_item_selection, parent, false);
        return new TempFeedInItemSelectionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final FeedItem fi = mFeedItemList.get(position);
        holder.tvSkuCode.setText(fi.getSkuCode());
        holder.tvSkuName.setText(fi.getSkuName());
        holder.view.setBackgroundColor(fi.getIsSelect() ?
                mContext.getResources().getColor(R.color.colorPrimaryLight): Color.WHITE);
        holder.cbSelect.setChecked(fi.getIsSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fi.setIsSelect(!fi.getIsSelect());
                holder.cbSelect.setChecked(fi.getIsSelect());
                holder.view.setBackgroundColor(fi.getIsSelect() ?
                        mContext.getResources().getColor(R.color.colorPrimaryLight): Color.WHITE);
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fi.setIsSelect(!fi.getIsSelect());
                holder.view.setBackgroundColor(fi.getIsSelect() ?
                        mContext.getResources().getColor(R.color.colorPrimaryLight): Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeedItemList == null ? 0 : mFeedItemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvSkuCode, tvSkuName;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView){
            super(itemView);
            view = itemView;
            tvSkuCode = itemView.findViewById(R.id.li_temp_feed_item_selection_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_temp_feed_item_selection_sku_name);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_item_selection_cb);
        }

    }
}
