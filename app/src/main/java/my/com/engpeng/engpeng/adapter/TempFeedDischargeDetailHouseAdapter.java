package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.model.HouseCode;

public class TempFeedDischargeDetailHouseAdapter extends RecyclerView.Adapter<TempFeedDischargeDetailHouseAdapter.HouseViewHolder>{

    private Context mContext;
    private List<HouseCode> mHouseList;

    public TempFeedDischargeDetailHouseAdapter(Context context, List<HouseCode> houseList) {
        this.mContext = context;
        this.mHouseList = houseList;
    }

    @Override
    public HouseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View view = li.inflate(R.layout.list_item_temp_feed_discharge_detail_house, parent, false);
        return new TempFeedDischargeDetailHouseAdapter.HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TempFeedDischargeDetailHouseAdapter.HouseViewHolder holder, int position) {
        final HouseCode hc = mHouseList.get(position);
        holder.tvHouseCode.setText("#" + hc.getHouseCode());
        holder.view.setBackgroundColor(hc.getIsSelect() ?
                mContext.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(hc.getIsSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetHouseList();
                hc.setIsSelect(!hc.getIsSelect());

                TempFeedDischargeDetailHouseAdapter.this.notifyDataSetChanged();
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetHouseList();
                hc.setIsSelect(!hc.getIsSelect());

                TempFeedDischargeDetailHouseAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHouseList == null ? 0 : mHouseList.size();
    }

    private void resetHouseList() {
        for (int i = 0; i < mHouseList.size(); i++) {
            HouseCode hc = mHouseList.get(i);
            hc.setIsSelect(false);
        }
    }

    class HouseViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tvHouseCode;
        CheckBox cbSelect;

        public HouseViewHolder(View itemView ){
            super(itemView);
            view = itemView;
            tvHouseCode = itemView.findViewById(R.id.li_temp_feed_discharge_detail_house_house_code);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_discharge_detail_house_cb);
        }
    }
}
