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

public class TempFeedInDetailFeedAdapter extends RecyclerView.Adapter<TempFeedInDetailFeedAdapter.ItemViewHolder> {

    private Context mContext;
    private List<FeedItem> mFeedItemList;

    public TempFeedInDetailFeedAdapter(Context context, List<FeedItem> feedItemList) {
        this.mContext = context;
        this.mFeedItemList = feedItemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_temp_feed_in_detail_feed, parent, false);
        return new TempFeedInDetailFeedAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final FeedItem fi = mFeedItemList.get(position);
        holder.tvSkuCode.setText(fi.getSkuCode());
        holder.tvSkuName.setText(fi.getSkuName());
        holder.view.setBackgroundColor(fi.getIsSelect() ?
                mContext.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(fi.getIsSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fi.setIsSelect(!fi.getIsSelect());

                TempFeedInDetailFeedAdapter.this.notifyDataSetChanged();
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fi.setIsSelect(!fi.getIsSelect());

                TempFeedInDetailFeedAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeedItemList == null ? 0 : mFeedItemList.size();
    }

    private void resetFeedItemList() {
        for (int i = 0; i < mFeedItemList.size(); i++) {
            FeedItem fi = mFeedItemList.get(i);
            fi.setIsSelect(false);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvSkuCode, tvSkuName;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvSkuCode = itemView.findViewById(R.id.li_temp_feed_detail_feed_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_temp_feed_detail_feed_sku_name);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_detail_feed_cb);
        }

    }
}
