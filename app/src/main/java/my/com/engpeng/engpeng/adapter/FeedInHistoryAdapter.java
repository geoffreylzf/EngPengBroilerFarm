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
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.utilities.PrintUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.data.EngPengContract.FeedInEntry;

public class FeedInHistoryAdapter extends RecyclerView.Adapter<FeedInHistoryAdapter.FeedInViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    private RecyclerView rv;

    private final FeedInHistoryAdapterListener fihaListener;

    public interface FeedInHistoryAdapterListener {
        void afterFeedInHistoryAdapterDelete();
    }

    public FeedInHistoryAdapter(Context context, Cursor cursor, SQLiteDatabase db, RecyclerView rv, FeedInHistoryAdapterListener fihaListener) {
        this.context = context;
        this.cursor = cursor;
        this.db = db;
        this.rv = rv;
        this.fihaListener = fihaListener;
    }

    @Override
    public FeedInViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_item_feed_in_history, parent, false);
        return new FeedInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedInViewHolder holder, final int position) {
        if (!cursor.moveToPosition(position)) return;
        final long feed_in_id = cursor.getLong(cursor.getColumnIndex(FeedInEntry._ID));
        String date = cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_RECORD_DATE));
        String document = cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_DOC_NUMBER));
        String truck_code = cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_TRUCK_CODE));
        double variance = cursor.getDouble(cursor.getColumnIndex(FeedInEntry.COLUMN_VARIANCE));
        final int is_upload = cursor.getInt(cursor.getColumnIndex(FeedInEntry.COLUMN_UPLOAD));

        String upload_str = "No";
        if (is_upload == 1) {
            upload_str = "Yes";
        }
        String variance_str = "Variance : ";
        if (variance > 0) {
            variance_str += "+";
        }
        variance_str += variance + " KG";

        holder.tvDate.setText(date);
        holder.tvDocument.setText(document);
        holder.tvTruckCode.setText(truck_code);
        holder.tvUpload.setText(upload_str);
        holder.tvVariance.setText(variance_str);
        holder.itemView.setTag(feed_in_id);

        holder.rvDetail.setLayoutManager(new LinearLayoutManager(context));
        Cursor detailCursor = FeedInDetailController.getAllByFeedInId(db, feed_in_id);
        FeedInHistoryDetailAdapter detailAdapter = new FeedInHistoryDetailAdapter(context, detailCursor, db);
        holder.rvDetail.setAdapter(detailAdapter);

        /*final boolean isExpanded = position == mExpandedPosition;
        holder.rvDetail.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                TransitionManager.beginDelayedTransition(rv, new AutoTransition());
                notifyDataSetChanged();
            }
        });*/

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
                                    FeedInController.remove(db, feed_in_id);
                                    FeedInDetailController.removeByFeedInId(db, feed_in_id);
                                    dialog.dismiss();
                                    fihaListener.afterFeedInHistoryAdapterDelete();
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
                String printText = PrintUtils.printFeedIn(db, feed_in_id);

                Intent ppIntent = new Intent(context, PrintPreview2Activity.class);
                ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
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

    class FeedInViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDocument, tvTruckCode, tvUpload, tvVariance;
        RecyclerView rvDetail;
        RelativeLayout rlDetail;
        LinearLayout ll;
        Button btnDelete, btnPrint;

        public FeedInViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.li_feed_in_history_ll);
            tvDate = itemView.findViewById(R.id.li_feed_in_history_tv_date);
            tvDocument = itemView.findViewById(R.id.li_feed_in_history_tv_document);
            tvTruckCode = itemView.findViewById(R.id.li_feed_in_history_tv_truck_code);
            tvUpload = itemView.findViewById(R.id.li_feed_in_history_tv_upload);
            tvVariance = itemView.findViewById(R.id.li_feed_in_history_tv_variance);

            rlDetail = itemView.findViewById(R.id.li_feed_in_history_rl_detail);
            rvDetail = itemView.findViewById(R.id.li_feed_in_history_rv_detail);

            btnDelete = itemView.findViewById(R.id.li_feed_in_history_btn_delete);
            btnPrint = itemView.findViewById(R.id.li_feed_in_history_btn_print);
        }
    }
}
