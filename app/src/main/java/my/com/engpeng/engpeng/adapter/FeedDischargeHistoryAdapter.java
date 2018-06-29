package my.com.engpeng.engpeng.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import my.com.engpeng.engpeng.PrintPreviewActivity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.FeedDischargeDetailController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.utilities.PrintUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;

public class FeedDischargeHistoryAdapter extends RecyclerView.Adapter<FeedDischargeHistoryAdapter.FeedDischargeViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    private RecyclerView rv;

    private final FeedDischargeHistoryAdapterListener fdhaListener;

    public interface FeedDischargeHistoryAdapterListener {
        void afterFeedDischargeHistoryAdapterDelete();
    }

    public FeedDischargeHistoryAdapter(Context context, Cursor cursor, SQLiteDatabase db, RecyclerView rv, FeedDischargeHistoryAdapterListener fdhaListener) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
        this.rv = rv;
        this.fdhaListener = fdhaListener;
    }

    @Override
    public FeedDischargeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_feed_discharge_history, parent, false);
        return new FeedDischargeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedDischargeViewHolder holder, final int position) {
        if (!cursor.moveToPosition(position)) return;
        final long feed_discharge_id = cursor.getLong(cursor.getColumnIndex(EngPengContract.FeedDischargeEntry._ID));
        String date = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedDischargeEntry.COLUMN_RECORD_DATE));
        String discharge_code = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedDischargeEntry.COLUMN_DISCHARGE_CODE));
        String truck_code = cursor.getString(cursor.getColumnIndex(EngPengContract.FeedDischargeEntry.COLUMN_TRUCK_CODE));
        final int is_upload = cursor.getInt(cursor.getColumnIndex(EngPengContract.FeedDischargeEntry.COLUMN_UPLOAD));

        String upload_str = "No";
        if (is_upload == 1) {
            upload_str = "Yes";
        }

        holder.tvDate.setText(date);
        holder.tvDischargeCode.setText(discharge_code);
        holder.tvTruckCode.setText(truck_code);
        holder.tvUpload.setText(upload_str);
        holder.itemView.setTag(feed_discharge_id);

        holder.rvDetail.setLayoutManager(new LinearLayoutManager(context));
        Cursor detailCursor = FeedDischargeDetailController.getAllByFeedDischargeId(db, feed_discharge_id);
        FeedDischargeHistoryDetailAdapter detailAdapter = new FeedDischargeHistoryDetailAdapter(context, detailCursor, db);
        holder.rvDetail.setAdapter(detailAdapter);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(rv, new Slide());
                holder.itemView.setActivated(!holder.itemView.isActivated());
                holder.rlDetail.setVisibility(holder.itemView.isActivated() ? View.VISIBLE : View.GONE);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_upload == 1) {
                    UIUtils.getMessageDialog(context, "Delete Failed", "Uploaded data is unable to delete").show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Delete This Feed In?");
                    alertDialog.setMessage("This action can't be undo. Do you still want to delete this feed in?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    TransitionManager.beginDelayedTransition(rv, new Slide());
                                    holder.itemView.setActivated(false);
                                    holder.rlDetail.setVisibility(View.GONE);
                                    FeedDischargeController.remove(db, feed_discharge_id);
                                    FeedDischargeDetailController.removeByFeedDischargeId(db, feed_discharge_id);
                                    dialog.dismiss();
                                    fdhaListener.afterFeedDischargeHistoryAdapterDelete();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        holder.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String printText = PrintUtils.printFeedDischarge(db, feed_discharge_id);
                String qr = FeedDischargeController.toQrData(db, feed_discharge_id);

                Intent ppIntent = new Intent(context, PrintPreviewActivity.class);
                ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                ppIntent.putExtra(I_KEY_PRINT_QR_TEXT, qr);
                context.startActivity(ppIntent);
            }
        });
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

    class FeedDischargeViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDischargeCode, tvTruckCode, tvUpload;
        RecyclerView rvDetail;
        RelativeLayout rlDetail;
        LinearLayout ll;
        Button btnDelete, btnPrint;

        public FeedDischargeViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_feed_discharge_history_ll);
            tvDate = itemView.findViewById(R.id.li_feed_discharge_history_tv_date);
            tvDischargeCode = itemView.findViewById(R.id.li_feed_discharge_history_tv_discharge_code);
            tvTruckCode = itemView.findViewById(R.id.li_feed_discharge_history_tv_truck_code);
            tvUpload = itemView.findViewById(R.id.li_feed_discharge_history_tv_upload);

            rlDetail = itemView.findViewById(R.id.li_feed_discharge_history_rl_detail);
            rvDetail = itemView.findViewById(R.id.li_feed_discharge_history_rv_detail);

            btnDelete = itemView.findViewById(R.id.li_feed_discharge_history_btn_delete);
            btnPrint = itemView.findViewById(R.id.li_feed_discharge_history_btn_print);
        }
    }
}
