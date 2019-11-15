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

import my.com.engpeng.engpeng.PrintPreview2Activity;
import my.com.engpeng.engpeng.R;
import my.com.engpeng.engpeng.controller.FeedReceiveController;
import my.com.engpeng.engpeng.controller.FeedReceiveDetailController;
import my.com.engpeng.engpeng.utilities.PrintUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.data.EngPengContract.FeedReceiveEntry;

public class FeedReceiveHistoryAdapter extends RecyclerView.Adapter<FeedReceiveHistoryAdapter.FeedReceiveViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    private RecyclerView rv;

    private final FeedReceiveHistoryAdapterListener frhaListener;

    public interface FeedReceiveHistoryAdapterListener {
        void afterFeedReceiveHistoryAdapterDelete();
    }

    public FeedReceiveHistoryAdapter(Context context, Cursor cursor, SQLiteDatabase db, RecyclerView rv, FeedReceiveHistoryAdapterListener frhaListener) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
        this.rv = rv;
        this.frhaListener = frhaListener;
    }

    @Override
    public FeedReceiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_feed_receive_history, parent, false);
        return new FeedReceiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedReceiveViewHolder holder, final int position) {
        if (!cursor.moveToPosition(position)) return;
        final long feed_receive_id = cursor.getLong(cursor.getColumnIndex(FeedReceiveEntry._ID));
        String date = cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_RECORD_DATE));
        String discharge_code = cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_DISCHARGE_CODE));
        String truck_code = cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_TRUCK_CODE));
        final int is_upload = cursor.getInt(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_UPLOAD));

        String upload_str = "No";
        if (is_upload == 1) {
            upload_str = "Yes";
        }

        holder.tvDate.setText(date);
        holder.tvDischargeCode.setText(discharge_code);
        holder.tvTruckCode.setText(truck_code);
        holder.tvUpload.setText(upload_str);
        holder.itemView.setTag(feed_receive_id);

        holder.rvDetail.setLayoutManager(new LinearLayoutManager(context));
        Cursor detailCursor = FeedReceiveDetailController.getAllByFeedReceiveId(db, feed_receive_id);
        FeedReceiveHistoryDetailAdapter detailAdapter = new FeedReceiveHistoryDetailAdapter(context, detailCursor, db);
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
                    alertDialog.setTitle("Delete This Feed Receive?");
                    alertDialog.setMessage("This action can't be undo. Do you still want to delete this feed in?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    TransitionManager.beginDelayedTransition(rv, new Slide());
                                    holder.itemView.setActivated(false);
                                    holder.rlDetail.setVisibility(View.GONE);
                                    FeedReceiveController.remove(db, feed_receive_id);
                                    FeedReceiveDetailController.removeByFeedReceiveId(db, feed_receive_id);
                                    dialog.dismiss();
                                    frhaListener.afterFeedReceiveHistoryAdapterDelete();
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
                String printText = PrintUtils.printFeedReceive(db, feed_receive_id);
                Intent ppIntent = new Intent(context, PrintPreview2Activity.class);
                ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                context.startActivity(ppIntent);
            }
        });
    }

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

    class FeedReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDischargeCode, tvTruckCode, tvUpload;
        RecyclerView rvDetail;
        RelativeLayout rlDetail;
        LinearLayout ll;
        Button btnDelete, btnPrint;

        public FeedReceiveViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_feed_receive_history_ll);
            tvDate = itemView.findViewById(R.id.li_feed_receive_history_tv_date);
            tvDischargeCode = itemView.findViewById(R.id.li_feed_receive_history_tv_discharge_code);
            tvTruckCode = itemView.findViewById(R.id.li_feed_receive_history_tv_truck_code);
            tvUpload = itemView.findViewById(R.id.li_feed_receive_history_tv_upload);

            rlDetail = itemView.findViewById(R.id.li_feed_receive_history_rl_detail);
            rvDetail = itemView.findViewById(R.id.li_feed_receive_history_rv_detail);

            btnDelete = itemView.findViewById(R.id.li_feed_receive_history_btn_delete);
            btnPrint = itemView.findViewById(R.id.li_feed_receive_history_btn_print);
        }
    }
}
