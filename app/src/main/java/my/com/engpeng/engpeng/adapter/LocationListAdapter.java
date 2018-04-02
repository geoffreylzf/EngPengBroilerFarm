package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import my.com.engpeng.engpeng.MainActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.controller.HouseController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.utilities.SharedPreferencesUtils;

/**
 * Created by Admin on 5/1/2018.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {
    private Context context;
    private Cursor cursor;
    private int company_id;
    private SQLiteDatabase db;

    public LocationListAdapter(Context context, Cursor cursor, SQLiteDatabase db, int company_id) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
        this.company_id = company_id;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_location, parent, false);
        return new LocationListAdapter.LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        int location_erp_id = cursor.getInt(cursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_ERP_ID));
        String location_name = cursor.getString(cursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

        holder.tvLocation.setText(location_name + "   (Num of Hse : " + HouseController.getCountByLocationId(db, location_erp_id) + ")");

        holder.itemView.setTag(location_erp_id);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView tvLocation;

        public LocationViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.location_name_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int location_id = (int) view.getTag();

                    SharedPreferencesUtils.saveCompanyIdLocationId(context, company_id, location_id);

                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mainIntent);
                }
            });
        }
    }
}
