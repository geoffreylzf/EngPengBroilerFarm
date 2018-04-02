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
import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 13/2/2018.
 */

public class TempCatchBTASummaryAdapter extends RecyclerView.Adapter<TempCatchBTASummaryAdapter.TempCatchBTADetailViewHolder> {

    private Context context;
    private Cursor cursor;

    public TempCatchBTASummaryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public TempCatchBTADetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_catch_bta_summary, parent, false);
        return new TempCatchBTADetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempCatchBTADetailViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        long id = cursor.getLong(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry._ID));
        String weight = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT));
        String qty = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY));
        String house = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_HOUSE_CODE));
        String cage_qty = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_CAGE_QTY));
        String with_cover_qty = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY));

        holder.tvNo.setText((cursor.getCount() - position) + "");
        holder.tvWeight.setText(weight);
        holder.tvQty.setText(qty);
        holder.tvHouse.setText(house);
        holder.tvCageQty.setText(cage_qty);
        holder.tvWithCoverQty.setText(with_cover_qty);

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

    class TempCatchBTADetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvNo, tvWeight, tvQty, tvHouse, tvCageQty, tvWithCoverQty;
        LinearLayout ll;

        public TempCatchBTADetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_catch_bta_summary_ll);
            tvNo = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_no);
            tvWeight = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_weight);
            tvQty = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_qty);
            tvHouse = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_house);
            tvCageQty = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_cage_qty);
            tvWithCoverQty = itemView.findViewById(R.id.li_temp_catch_bta_summary_tv_with_cover_qty);
        }
    }
}
