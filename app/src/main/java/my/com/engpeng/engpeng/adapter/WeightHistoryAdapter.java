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

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.WeightActivity;

import static my.com.engpeng.engpeng.Global.I_KEY_WEIGHT;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 15/1/2018.
 */

public class WeightHistoryAdapter extends RecyclerView.Adapter<WeightHistoryAdapter.WeightViewHolder>{

    private Context context;
    private Cursor cursor;

    public WeightHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_weight_history, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(WeightEntry._ID));
        String date = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_DATE));
        String time = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_TIME));
        String feed = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_FEED));
        String day = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_DAY));

        int is_upload = cursor.getInt(cursor.getColumnIndex(WeightEntry.COLUMN_UPLOAD));
        String upload_str = "No";
        if(is_upload == 1){
            upload_str = "Yes";
        }

        holder.tvDate.setText(date);
        holder.tvTime.setText(time);
        holder.tvFeed.setText(feed);
        holder.tvDay.setText(day);
        holder.tvUpload.setText(upload_str);

        holder.itemView.setTag(id);

        /*if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorAccentXLight));
        }else{
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorLayoutBackground));
        }*/
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

    class WeightViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvTime, tvFeed, tvDay, tvUpload;
        LinearLayout ll;

        public WeightViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_weight_history_ll);
            tvDate = itemView.findViewById(R.id.li_weight_history_tv_date);
            tvTime = itemView.findViewById(R.id.li_weight_history_tv_time);
            tvFeed = itemView.findViewById(R.id.li_weight_history_tv_feed);
            tvDay = itemView.findViewById(R.id.li_weight_history_tv_day);
            tvUpload = itemView.findViewById(R.id.li_weight_history_tv_upload);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent weightIntent = new Intent(context, WeightActivity.class);
                    weightIntent.putExtra(I_KEY_WEIGHT, (Long) view.getTag());
                    context.startActivity(weightIntent);
                }
            });
        }
    }
}
