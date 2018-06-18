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
import my.com.engpeng.engpeng.model.Compartment;
import my.com.engpeng.engpeng.model.FeedItem;

public class TempFeedInDetailCompartmentAdapter extends RecyclerView.Adapter<TempFeedInDetailCompartmentAdapter.ItemViewHolder>{

    private Context context;
    private List<Compartment> compartmentList;

    public TempFeedInDetailCompartmentAdapter(Context context, List<Compartment> compartmentList) {
        this.context = context;
        this.compartmentList = compartmentList;
    }

    @Override
    public TempFeedInDetailCompartmentAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_feed_in_detail_compartment, parent, false);
        return new TempFeedInDetailCompartmentAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TempFeedInDetailCompartmentAdapter.ItemViewHolder holder, int position) {
        final Compartment compartment = compartmentList.get(position);
        holder.tvCompartmentNo.setText(compartment.getCompartmentNo());
        holder.tvWeight.setText(compartment.getWeight() + " KG");
        holder.view.setBackgroundColor(compartment.isSelect() ?
                context.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(compartment.isSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCompartmentList();
                compartment.setSelect(!compartment.isSelect());

                TempFeedInDetailCompartmentAdapter.this.notifyDataSetChanged();
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCompartmentList();
                compartment.setSelect(!compartment.isSelect());

                TempFeedInDetailCompartmentAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return compartmentList == null ? 0 : compartmentList.size();
    }

    private void resetCompartmentList() {
        for (int i = 0; i < compartmentList.size(); i++) {
            Compartment compartment = compartmentList.get(i);
            compartment.setSelect(false);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvCompartmentNo, tvWeight;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvCompartmentNo = itemView.findViewById(R.id.li_temp_feed_detail_compartment_tv_c_no);
            tvWeight = itemView.findViewById(R.id.li_temp_feed_detail_compartment_tv_weight);
            cbSelect = itemView.findViewById(R.id.li_temp_feed_detail_compartment_cb);
        }

    }
}
