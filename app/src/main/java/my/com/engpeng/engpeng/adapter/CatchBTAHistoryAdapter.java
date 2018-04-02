package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.CatchBTAActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengContract.CatchBTAEntry;

import static my.com.engpeng.engpeng.Global.I_KEY_CATCH_BTA;

/**
 * Created by Admin on 27/2/2018.
 */

public class CatchBTAHistoryAdapter extends RecyclerView.Adapter<CatchBTAHistoryAdapter.CatchBTAViewHolder>{

    private Context context;
    private Cursor cursor;

    public CatchBTAHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public CatchBTAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_catch_bta_history, parent, false);
        return new CatchBTAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CatchBTAViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(CatchBTAEntry._ID));
        String date = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_RECORD_DATE));
        String document = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_TYPE))
                + "-"
                + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_NUMBER));
        String type = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TYPE));
        if(type.equals("B")){
            type = "C";
        }

        String truck_code = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TRUCK_CODE));
        int is_upload = cursor.getInt(cursor.getColumnIndex(CatchBTAEntry.COLUMN_UPLOAD));
        String upload_str = "No";
        if(is_upload == 1){
            upload_str = "Yes";
        }

        holder.tvDate.setText(date);
        holder.tvDocument.setText(document);
        holder.tvType.setText(type);
        holder.tvTruckCode.setText(truck_code);
        holder.tvUpload.setText(upload_str);

        holder.itemView.setTag(id);
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

    class CatchBTAViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvDocument, tvType, tvTruckCode, tvUpload;
        LinearLayout ll;

        public CatchBTAViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_catch_bta_history_ll);
            tvDate = itemView.findViewById(R.id.li_catch_bta_history_tv_date);
            tvDocument = itemView.findViewById(R.id.li_catch_bta_history_tv_document);
            tvType = itemView.findViewById(R.id.li_catch_bta_history_tv_type);
            tvTruckCode = itemView.findViewById(R.id.li_catch_bta_history_tv_truck_code);
            tvUpload = itemView.findViewById(R.id.li_catch_bta_history_tv_upload);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent catchBTAIntent = new Intent(context, CatchBTAActivity.class);
                    catchBTAIntent.putExtra(I_KEY_CATCH_BTA, (Long) view.getTag());
                    context.startActivity(catchBTAIntent);
                }
            });
        }
    }
}
