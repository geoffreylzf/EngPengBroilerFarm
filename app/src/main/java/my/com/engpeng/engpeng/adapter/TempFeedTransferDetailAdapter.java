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

public class TempFeedTransferDetailAdapter extends RecyclerView.Adapter<TempFeedTransferDetailAdapter.ItemViewHolder> {

    private Context mContext;
    private List<FeedItem> mFeedItemList;

    public TempFeedTransferDetailAdapter(Context context, List<FeedItem> feedItemList) {
        this.mContext = context;
        this.mFeedItemList = feedItemList;
    }

    @Override
    public TempFeedTransferDetailAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_temp_feed_transfer_detail, parent, false);
        return new TempFeedTransferDetailAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TempFeedTransferDetailAdapter.ItemViewHolder holder, int position) {
        final FeedItem fi = mFeedItemList.get(position);
        holder.tvSkuCode.setText(fi.getSkuCode());
        holder.tvSkuName.setText(fi.getSkuName());
        holder.view.setBackgroundColor(fi.isSelect() ?
                mContext.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(fi.isSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fi.setSelect(!fi.isSelect());

                TempFeedTransferDetailAdapter.this.notifyDataSetChanged();
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFeedItemList();
                fi.setSelect(!fi.isSelect());

                TempFeedTransferDetailAdapter.this.notifyDataSetChanged();
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
            fi.setSelect(false);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvSkuCode, tvSkuName;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvSkuCode = itemView.findViewById(R.id.li_temp_feed_transfer_detail_feed_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_temp_feed_transfer_detail_feed_sku_name);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_transfer_detail_feed_cb);
        }

    }
}
