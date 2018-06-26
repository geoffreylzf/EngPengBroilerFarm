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

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedDischargeSummaryAdapter extends RecyclerView.Adapter<TempFeedDischargeSummaryAdapter.TempFeedDischargeDetailViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;

    public TempFeedDischargeSummaryAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
    }

    @Override
    public TempFeedDischargeSummaryAdapter.TempFeedDischargeDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_feed_discharge_summary, parent, false);
        return new TempFeedDischargeSummaryAdapter.TempFeedDischargeDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempFeedDischargeDetailViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        long id = cursor.getLong(cursor.getColumnIndex(TempFeedInDetailEntry._ID));
        String house_code = cursor.getString(cursor.getColumnIndex(TempFeedInDetailEntry.COLUMN_HOUSE_CODE));
        int item_packing_id = cursor.getInt(cursor.getColumnIndex(TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID));

        String weight = cursor.getString(cursor.getColumnIndex(TempFeedInDetailEntry.COLUMN_WEIGHT));

        String sku_code = "";
        String sku_name = "";
        Cursor cFeedItem = FeedItemController.getByErpId(db, item_packing_id);
        if (cFeedItem.moveToFirst()) {
            sku_code = cFeedItem.getString(cFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
            sku_name = cFeedItem.getString(cFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
        }else{
            sku_code = "New Feed";
            sku_name = "ITEM_PACKING_ID: " + item_packing_id;
        }

        holder.tvNo.setText((cursor.getCount() - position) + "");
        holder.tvHouseCode.setText(house_code);
        holder.tvSkuCode.setText(sku_code);
        holder.tvSkuName.setText(sku_name);
        holder.tvWeight.setText(weight);

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

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class TempFeedDischargeDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo, tvHouseCode, tvSkuCode, tvSkuName, tvWeight;
        LinearLayout ll;

        public TempFeedDischargeDetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_feed_discharge_summary_ll);
            tvNo = itemView.findViewById(R.id.li_temp_feed_discharge_summary_tv_no);
            tvHouseCode = itemView.findViewById(R.id.li_temp_feed_discharge_summary_tv_house_code);
            tvSkuCode = itemView.findViewById(R.id.li_temp_feed_discharge_summary_tv_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_temp_feed_discharge_summary_tv_sku_name);
            tvWeight = itemView.findViewById(R.id.li_temp_feed_discharge_summary_tv_weight);
        }
    }
}
