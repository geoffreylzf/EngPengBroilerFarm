package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.R;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 8/1/2018.
 */

public class MortalityHistoryAdapter extends RecyclerView.Adapter<MortalityHistoryAdapter.MortalityViewHolder> {

    private Context context;
    private Cursor cursor;

    public MortalityHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public MortalityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_mortality_history, parent, false);
        return new MortalityHistoryAdapter.MortalityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MortalityViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(MortalityEntry._ID));
        String dateStr = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_RECORD_DATE));
        String m_q = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_M_Q));
        String r_q = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_R_Q));
        int is_upload = cursor.getInt(cursor.getColumnIndex(MortalityEntry.COLUMN_UPLOAD));

        String str_upload = "No";
        if(is_upload == 1){
            str_upload = "Yes";
        }

        holder.tvDate.setText(dateStr);
        holder.tvMQ.setText(m_q);
        holder.tvRQ.setText(r_q);
        holder.tvUpload.setText(str_upload);

        holder.itemView.setTag(id);

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryXLight));
        }else{
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

    class MortalityViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvMQ, tvRQ, tvUpload;
        LinearLayout ll;

        public MortalityViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_mortality_history_ll);
            tvDate = itemView.findViewById(R.id.li_mortality_history_tv_date);
            tvMQ = itemView.findViewById(R.id.li_mortality_history_tv_m_q);
            tvRQ = itemView.findViewById(R.id.li_mortality_history_tv_r_q);
            tvUpload = itemView.findViewById(R.id.li_mortality_history_tv_upload);

        }
    }
}
