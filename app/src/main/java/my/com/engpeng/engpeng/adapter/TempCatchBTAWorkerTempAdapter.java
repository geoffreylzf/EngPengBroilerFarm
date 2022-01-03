package my.com.engpeng.engpeng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.model.TempBtaWorker;

public class TempCatchBTAWorkerTempAdapter extends RecyclerView.Adapter<TempCatchBTAWorkerTempAdapter.TempCatchBTAWorkerTempViewHolder> {

    private Context context;
    private List<TempBtaWorker> tempBtaWorkerList;

    public TempCatchBTAWorkerTempAdapter(Context context, List<TempBtaWorker> tempBtaWorkerList) {
        this.context = context;
        this.tempBtaWorkerList = tempBtaWorkerList;
    }

    @Override
    public TempCatchBTAWorkerTempViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_temp_catch_bta_worker_temp, parent, false);
        return new TempCatchBTAWorkerTempAdapter.TempCatchBTAWorkerTempViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TempCatchBTAWorkerTempAdapter.TempCatchBTAWorkerTempViewHolder holder, int position) {
        final TempBtaWorker temp = tempBtaWorkerList.get(position);
        String name = temp.getWorkerName();
        if (temp.getPersonStaffId() == null) {
            name = "* " + name;
        }
        holder.tvWorkerName.setText(name);
        holder.itemView.setTag(temp.getId());
    }

    @Override
    public int getItemCount() {
        return tempBtaWorkerList == null ? 0 : tempBtaWorkerList.size();
    }

    public void setList(List<TempBtaWorker> newList) {
        this.tempBtaWorkerList = newList;
        this.notifyDataSetChanged();
    }

    class TempCatchBTAWorkerTempViewHolder extends RecyclerView.ViewHolder {

        TextView tvWorkerName;
        LinearLayout ll;

        public TempCatchBTAWorkerTempViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_temp_catch_bta_worker_temp_ll);
            tvWorkerName = itemView.findViewById(R.id.li_temp_catch_bta_worker_temp_tv_worker_name);
        }
    }
}
