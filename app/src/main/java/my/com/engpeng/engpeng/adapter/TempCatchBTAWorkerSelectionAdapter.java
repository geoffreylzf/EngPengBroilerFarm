package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.model.Compartment;
import my.com.engpeng.engpeng.model.PersonStaffSelection;

public class TempCatchBTAWorkerSelectionAdapter extends RecyclerView.Adapter<TempCatchBTAWorkerSelectionAdapter.ItemViewHolder> {

    final private Context context;
    private List<PersonStaffSelection> personStaffSelectionList;
    final private TempCatchBTAWorkerSelectionAdapterListener tcbwsaListener;

    public TempCatchBTAWorkerSelectionAdapter(Context context, List<PersonStaffSelection> personStaffSelectionList, TempCatchBTAWorkerSelectionAdapterListener tcbwsaListener) {
        this.context = context;
        this.personStaffSelectionList = personStaffSelectionList;
        this.tcbwsaListener = tcbwsaListener;
    }

    public interface TempCatchBTAWorkerSelectionAdapterListener {
        void onClick(int id, String workerName);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_catch_bta_worker_selection, parent, false);
        return new TempCatchBTAWorkerSelectionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final PersonStaffSelection ps = personStaffSelectionList.get(position);

        holder.tvStaffName.setText(ps.getStaffName());
        holder.tvStaffCode.setText(ps.getStaffCode());
        holder.view.setBackgroundColor(ps.isSelect() ?
                context.getResources().getColor(R.color.colorPrimaryLight) : Color.WHITE);
        holder.cbSelect.setChecked(ps.isSelect());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcbwsaListener.onClick(ps.getId(), ps.getStaffName());
            }
        });

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcbwsaListener.onClick(ps.getId(), ps.getStaffName());
            }
        });
    }

    public int getItemCount() {
        return personStaffSelectionList == null ? 0 : personStaffSelectionList.size();
    }

    public void setList(List<PersonStaffSelection> newList) {
        this.personStaffSelectionList = newList;
        this.notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvStaffName, tvStaffCode;
        CheckBox cbSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvStaffName = itemView.findViewById(R.id.li_temp_catch_bta_worker_selection_tv_staff_name);
            tvStaffCode = itemView.findViewById(R.id.li_temp_catch_bta_worker_selection_tv_staff_code);
            cbSelect = itemView.findViewById(R.id.li_temp_catch_bta_worker_selection_cb);
        }

    }
}
