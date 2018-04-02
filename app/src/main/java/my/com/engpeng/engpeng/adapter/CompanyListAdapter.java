package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import my.com.engpeng.engpeng.LocationListActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.Global.*;

/**
 * Created by Admin on 5/1/2018.
 */

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder>{
    private Context mContext;
    private Cursor mCursor;

    public CompanyListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_company, parent, false);
        return new CompanyListAdapter.CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;
        String company_erp_id = mCursor.getString(mCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_ERP_ID));
        String company_name = mCursor.getString(mCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

        holder.tvCompanyName.setText(company_name);

        holder.itemView.setTag(company_erp_id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCompanyName;

        public CompanyViewHolder(View itemView) {
            super(itemView);
            tvCompanyName = (TextView) itemView.findViewById(R.id.company_name_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent locationListIntent = new Intent(mContext, LocationListActivity.class);
                    locationListIntent.putExtra(I_KEY_COMPANY, (String) view.getTag());
                    mContext.startActivity(locationListIntent);
                }
            });
        }
    }
}
