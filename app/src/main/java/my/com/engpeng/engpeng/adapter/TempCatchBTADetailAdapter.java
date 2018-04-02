package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 7/3/2018.
 */

public class TempCatchBTADetailAdapter extends RecyclerView.Adapter<TempCatchBTADetailAdapter.TempCatchBTADetailViewHolder> {

    private Context context;
    private Cursor cursor;

    public TempCatchBTADetailAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public TempCatchBTADetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_catch_bta_detail, parent, false);
        return new TempCatchBTADetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempCatchBTADetailViewHolder holder, int position) {

        if (!cursor.moveToPosition(position)) return;

        long id = cursor.getLong(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry._ID));
        String weight = String.format(Locale.getDefault(), "%.2f", cursor.getDouble(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT)));
        String house = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_HOUSE_CODE));

        holder.tvNo.setText((cursor.getCount() - position) + "");
        holder.tvWeight.setText(weight);
        holder.tvHouse.setText(house);

        holder.itemView.setTag(id);

        if ((cursor.getCount() - position) % 2 == 0) {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryXLight));
        } else {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.colorLayoutBackground));
        }

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class TempCatchBTADetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvNo, tvHouse, tvWeight;
        LinearLayout ll;

        public TempCatchBTADetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_catch_bta_detail_ll);
            tvNo = itemView.findViewById(R.id.li_temp_catch_bta_detail_tv_no);
            tvHouse = itemView.findViewById(R.id.li_temp_catch_bta_detail_tv_house);
            tvWeight = itemView.findViewById(R.id.li_temp_catch_bta_detail_tv_weight);
        }
    }
}
