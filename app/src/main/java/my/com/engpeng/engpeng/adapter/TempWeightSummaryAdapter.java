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
 * Created by Admin on 15/1/2018.
 */

public class TempWeightSummaryAdapter extends RecyclerView.Adapter<TempWeightSummaryAdapter.TempWeightDetailViewHolder>{

    private Context context;
    private Cursor cursor;

    public TempWeightSummaryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public TempWeightDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_weight_summary, parent, false);
        return new TempWeightDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempWeightDetailViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(TempWeightDetailEntry._ID));
        String section = cursor.getString(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_SECTION));
        String wgt = cursor.getString(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_WEIGHT));
        String qty = cursor.getString(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_QTY));
        String gender = cursor.getString(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_GENDER));

        holder.tvSection.setText(section);
        holder.tvWgt.setText(wgt);
        holder.tvQty.setText(qty);
        holder.tvGender.setText(gender);

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

    class TempWeightDetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvSection, tvWgt, tvQty, tvGender;
        LinearLayout ll;

        public TempWeightDetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_weight_summary_ll);
            tvSection = itemView.findViewById(R.id.li_temp_weight_summary_tv_section);
            tvWgt = itemView.findViewById(R.id.li_temp_weight_summary_tv_weight);
            tvQty = itemView.findViewById(R.id.li_temp_weight_summary_tv_quantity);
            tvGender = itemView.findViewById(R.id.li_temp_weight_summary_tv_gender);
        }
    }
}
