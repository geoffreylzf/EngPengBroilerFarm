package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.PrintPreviewActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.utilities.PrintUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class FeedTransferHistoryAdapter extends RecyclerView.Adapter<FeedTransferHistoryAdapter.ItemViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;

    public FeedTransferHistoryAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_feed_transfer_history, parent, false);
        return new FeedTransferHistoryAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        final long feed_transfer_id = cursor.getLong(cursor.getColumnIndex(FeedTransferEntry._ID));
        String record_date = cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RECORD_DATE));
        String discharge_house = cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_DISCHARGE_HOUSE));
        String receive_house = cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RECEIVE_HOUSE));
        int item_packing_id = cursor.getInt(cursor.getColumnIndex(FeedTransferEntry.COLUMN_ITEM_PACKING_ID));
        String weight = cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_WEIGHT));
        int is_upload = cursor.getInt(cursor.getColumnIndex(FeedTransferEntry.COLUMN_UPLOAD));

        Cursor cFeedItem = FeedItemController.getByErpId(db, item_packing_id);

        String sku_code = "Unknown SKU Code (ID: " + item_packing_id + ")";
        String sku_name = "Unknown SKU Name";
        if (cFeedItem.moveToFirst()) {
            sku_code = cFeedItem.getString(cFeedItem.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_CODE));
            sku_name = cFeedItem.getString(cFeedItem.getColumnIndex(EngPengContract.FeedItemEntry.COLUMN_SKU_NAME));
        }

        String str_upload = "No";
        if (is_upload == 1) {
            str_upload = "Yes";
        }

        holder.tvDate.setText(record_date);
        holder.tvHouseCode.setText(discharge_house + " > " + receive_house);
        holder.tvSkuCode.setText(sku_code);
        holder.tvSkuName.setText(sku_name);
        holder.tvWeight.setText(weight);
        holder.tvUpload.setText(str_upload);

        holder.itemView.setTag(feed_transfer_id);

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryXLight));
        } else {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorLayoutBackground));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String printText = PrintUtils.printFeedTransfer(db, feed_transfer_id);
                Intent ppIntent = new Intent(context, PrintPreviewActivity.class);
                ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                context.startActivity(ppIntent);
            }
        });
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHouseCode, tvSkuCode, tvSkuName, tvWeight, tvUpload;
        LinearLayout ll;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_feed_transfer_history_ll);
            tvDate = itemView.findViewById(R.id.li_feed_transfer_history_tv_date);
            tvHouseCode = itemView.findViewById(R.id.li_feed_transfer_history_tv_house_code);
            tvSkuCode = itemView.findViewById(R.id.li_feed_transfer_history_tv_sku_code);
            tvSkuName = itemView.findViewById(R.id.li_feed_transfer_history_tv_sku_name);
            tvWeight = itemView.findViewById(R.id.li_feed_transfer_history_tv_weight);
            tvUpload = itemView.findViewById(R.id.li_feed_transfer_history_tv_upload);
        }
    }
}
