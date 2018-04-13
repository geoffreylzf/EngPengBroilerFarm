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

public class TempWeightDetailAdapter extends RecyclerView.Adapter<TempWeightDetailAdapter.TempWeightDetailViewHolder> {

    private Context context;
    private Cursor cursor;

    public TempWeightDetailAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public TempWeightDetailAdapter.TempWeightDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_weight_detail, parent, false);
        return new TempWeightDetailAdapter.TempWeightDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempWeightDetailAdapter.TempWeightDetailViewHolder holder, int position) {

        if (!cursor.moveToPosition(position)) return;

        long id = cursor.getLong(cursor.getColumnIndex(EngPengContract.TempWeightDetailEntry._ID));
        String weight = cursor.getString(cursor.getColumnIndex(EngPengContract.TempWeightDetailEntry.COLUMN_WEIGHT));
        String gender = cursor.getString(cursor.getColumnIndex(EngPengContract.TempWeightDetailEntry.COLUMN_GENDER));

        holder.tvNo.setText((cursor.getCount() - position) + "");
        holder.tvWeight.setText(weight);
        holder.tvGender.setText(gender);

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

    class TempWeightDetailViewHolder extends RecyclerView.ViewHolder {

        TextView tvNo, tvWeight, tvGender;
        LinearLayout ll;

        public TempWeightDetailViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_weight_detail_ll);
            tvNo = itemView.findViewById(R.id.li_temp_weight_detail_tv_no);
            tvWeight = itemView.findViewById(R.id.li_temp_weight_detail_tv_weight);
            tvGender = itemView.findViewById(R.id.li_temp_weight_detail_tv_gender);
        }
    }
}
