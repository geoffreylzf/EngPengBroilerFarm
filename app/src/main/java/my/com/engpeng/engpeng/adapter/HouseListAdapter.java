package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.MortalityActivity;
import my.com.engpeng.engpeng.MortalityHistoryActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.TempWeightHeadActivity;
import my.com.engpeng.engpeng.WeightHistoryActivity;
import my.com.engpeng.engpeng.controller.MortalityController;
import my.com.engpeng.engpeng.controller.WeightController;

import static my.com.engpeng.engpeng.Global.*;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 4/1/2018.
 */

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.HouseViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private SQLiteDatabase mdb;
    private String module;

    public HouseListAdapter(Context context, Cursor cursor, SQLiteDatabase db, String module) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mdb = db;
        this.module = module;
    }

    @Override
    public HouseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_house, parent, false);
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HouseViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;
        int company_id = sCompanyId;
        int location_id = sLocationId;
        int house_code = mCursor.getInt(mCursor.getColumnIndex(HouseEntry.COLUMN_HOUSE_CODE));

        if (module.equals(MODULE_MORTALITY)) {

            holder.tvHouseCode.setText("#" + String.valueOf(house_code) + "   ("
                    + MortalityController.getLastDayByCLHU(mdb, company_id, location_id, house_code)
                    + MortalityController.getLastMRByCLHU(mdb, company_id, location_id, house_code)
                    + ")");
        } else if (module.equals(MODULE_WEIGHT)) {
            holder.tvHouseCode.setText("#" + String.valueOf(house_code)
                    + "   (" + WeightController.getLastDayByCLHU(mdb, company_id, location_id, house_code, 0)
                    + ")");
        }

        holder.itemView.setTag(String.valueOf(house_code));
        holder.ll.setTag(String.valueOf(house_code));
        holder.btnHistory.setTag(String.valueOf(house_code));

        /*if (position % 2 == 0) {
            holder.ll.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryXLight));
        } else {
            holder.ll.setBackgroundColor(mContext.getResources().getColor(R.color.colorLayoutBackground));
        }*/
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class HouseViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;
        TextView tvHouseCode, tvCircleHouseCode;
        Button btnHistory;

        public HouseViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_house_ll);
            tvCircleHouseCode = itemView.findViewById(R.id.house_code_circle_text_view);
            tvHouseCode = itemView.findViewById(R.id.house_code_text_view);
            btnHistory = itemView.findViewById(R.id.li_house_history);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (module.equals(MODULE_MORTALITY)) {

                        Intent mortalityIntent = new Intent(mContext, MortalityActivity.class);
                        mortalityIntent.putExtra(I_KEY_HOUSE_CODE, Integer.parseInt((String) view.getTag()));
                        mContext.startActivity(mortalityIntent);

                    }  else if (module.equals(MODULE_WEIGHT)) {

                        Intent tempWeightHeadIntent = new Intent(mContext, TempWeightHeadActivity.class);
                        tempWeightHeadIntent.putExtra(I_KEY_HOUSE_CODE, Integer.parseInt((String) view.getTag()));
                        mContext.startActivity(tempWeightHeadIntent);

                    }
                }
            });

            btnHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (module.equals(MODULE_MORTALITY)) {

                        Intent mortalityHistoryIntent = new Intent(mContext, MortalityHistoryActivity.class);
                        mortalityHistoryIntent.putExtra(I_KEY_HOUSE_CODE, Integer.parseInt((String) view.getTag()));
                        mContext.startActivity(mortalityHistoryIntent);

                    }  else if (module.equals(MODULE_WEIGHT)) {

                        Intent weightHistoryIntent = new Intent(mContext, WeightHistoryActivity.class);
                        weightHistoryIntent.putExtra(I_KEY_HOUSE_CODE, Integer.parseInt((String) view.getTag()));
                        mContext.startActivity(weightHistoryIntent);
                    }
                }
            });
        }
    }
}
