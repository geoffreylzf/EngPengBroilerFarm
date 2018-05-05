package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.data.EngPengContract;

public class FeedInHistoryDetailAdapter extends RecyclerView.Adapter<FeedInHistoryDetailAdapter.FeedInDetailViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;

    public FeedInHistoryDetailAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
    }

    @Override
    public FeedInHistoryDetailAdapter.FeedInDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_feed_in_history_detail, parent, false);
        return new FeedInHistoryDetailAdapter.FeedInDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedInDetailViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        long id = cursor.getLong(cursor.getColumnIndex(EngPengContract.FeedInDetailEntry._ID));
        String house_code = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedInDetailEntry.COLUMN_HOUSE_CODE));
        int item_packing_id = cursor.getInt(cursor.getColumnIndex(EngPengContract.FeedInDetailEntry.COLUMN_ITEM_PACKING_ID));
        String qty = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedInDetailEntry.COLUMN_QTY));

        Cursor cFeedItem = FeedItemController.getByErpId(db, item_packing_id);

        String sku_code = "Unknown SKU Code (ID: " + item_packing_id + ")";
        String sku_name = "Unknown SKU Name";
        if (cFeedItem.moveToFirst()) {
            sku_code = cFeedItem.getString(cFeedItem.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_CODE));
            sku_name = cFeedItem.getString(cFeedItem.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_NAME));
        }

        holder.tvNo.setText((cursor.getCount() - position) + "");
        holder.tvHouseCode.setText(house_code);
        holder.tvSkuCode.setText(sku_code);
        holder.tvSkuName.setText(sku_name);
        holder.tvQty.setText(qty);

        holder.itemView.setTag(id);

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryXLight));
        } else {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorLayoutBackground));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class FeedInDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo, tvHouseCode, tvSkuCode, tvSkuName, tvQty;
        LinearLayout ll;

        public FeedInDetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_feed_in_history_detail_ll);
            tvNo = itemView.findViewById(R.id.li_feed_in_history_detail_tv_no);
            tvHouseCode = itemView.findViewById(R.id.li_feed_in_history_detail_tv_house_code);
            tvSkuCode = itemView.findViewById(R.id.li_feed_in_history_detail_tv_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_feed_in_history_detail_tv_sku_name);
            tvQty = itemView.findViewById(R.id.li_feed_in_history_detail_tv_qty);
        }
    }
}
