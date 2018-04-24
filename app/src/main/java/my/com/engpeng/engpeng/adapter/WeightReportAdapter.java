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
import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.controller.WeightDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;

public class WeightReportAdapter extends RecyclerView.Adapter<WeightReportAdapter.WeightReportViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;

    public WeightReportAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
    }

    @Override
    public WeightReportAdapter.WeightReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_weight_report, parent, false);
        return new WeightReportAdapter.WeightReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeightReportAdapter.WeightReportViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        long id = cursor.getLong(cursor.getColumnIndex(EngPengContract.WeightEntry._ID));
        int location_id = cursor.getInt(cursor.getColumnIndex(EngPengContract.WeightEntry.COLUMN_LOCATION_ID));
        int house_code = cursor.getInt(cursor.getColumnIndex(EngPengContract.WeightEntry.COLUMN_HOUSE_CODE));
        int day = cursor.getInt(cursor.getColumnIndex(EngPengContract.WeightEntry.COLUMN_DAY));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));

        int ttl_m_weight = WeightDetailController.getTotalWgtByGenderWeightId(db, id, "M");
        int ttl_m_qty = WeightDetailController.getTotalQtyByGenderWeightId(db, id, "M");
        double m_avg = (double) ttl_m_weight / (double) ttl_m_qty;

        int ttl_f_weight = WeightDetailController.getTotalWgtByGenderWeightId(db, id, "F");
        int ttl_f_qty = WeightDetailController.getTotalQtyByGenderWeightId(db, id, "F");
        double f_avg = (double) ttl_f_weight / (double) ttl_f_qty;

        int ttl_weight = WeightDetailController.getTotalWgtByWeightId(db, id);
        int ttl_qty = WeightDetailController.getTotalQtyByWeightId(db, id);
        double avg = (double) ttl_weight / (double) ttl_qty;

        holder.tvLoc.setText(location_code);
        holder.tvHse.setText(house_code + "");
        holder.tvDay.setText(day + "");
        holder.tvMAvg.setText(String.format("%.2f", m_avg));
        holder.tvFAvg.setText(String.format("%.2f", f_avg));
        holder.tvAvg.setText(String.format("%.2f", avg));

        holder.itemView.setTag(id);

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorAccentXLight));
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

    class WeightReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoc, tvHse, tvDay, tvMAvg, tvFAvg, tvAvg;
        LinearLayout ll;

        public WeightReportViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_weight_report_ll);
            tvLoc = itemView.findViewById(R.id.li_weight_report_tv_loc);
            tvHse = itemView.findViewById(R.id.li_weight_report_tv_hse);
            tvDay = itemView.findViewById(R.id.li_weight_report_tv_day);
            tvMAvg = itemView.findViewById(R.id.li_weight_report_tv_m_avg);
            tvFAvg = itemView.findViewById(R.id.li_weight_report_tv_f_avg);
            tvAvg = itemView.findViewById(R.id.li_weight_report_tv_avg);
        }
    }
}
