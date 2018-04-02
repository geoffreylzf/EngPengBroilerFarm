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
 * Created by Admin on 16/1/2018.
 */

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightDetailViewHolder> {

    private Context context;
    private Cursor cursor;

    public WeightAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public WeightDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_weight, parent, false);
        return new WeightAdapter.WeightDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeightDetailViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(WeightDetailEntry._ID));
        String section = cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_SECTION));
        String wgt = cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT));
        String qty = cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_QTY));
        String gender = cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_GENDER));

        holder.tvSection.setText(section);
        holder.tvWgt.setText(wgt);
        holder.tvQty.setText(qty);
        holder.tvGender.setText(gender);

        holder.itemView.setTag(id);

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorAccentXLight));
        }else{
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorLayoutBackground));
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class WeightDetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvSection, tvWgt, tvQty, tvGender;
        LinearLayout ll;

        public WeightDetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_weight_ll);
            tvSection = itemView.findViewById(R.id.li_weight_tv_section);
            tvWgt = itemView.findViewById(R.id.li_weight_tv_weight);
            tvQty = itemView.findViewById(R.id.li_weight_tv_quantity);
            tvGender = itemView.findViewById(R.id.li_weight_tv_gender);
        }
    }
}
