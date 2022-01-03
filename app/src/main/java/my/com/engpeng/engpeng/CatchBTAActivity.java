package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.CatchBTAAdapter;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.CatchBTAWorkerController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengContract.CatchBTAEntry;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.PrintUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.I_KEY_CATCH_BTA;
import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_QR_TEXT;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;
import static my.com.engpeng.engpeng.Global.MODULE_CATCH_BTA;

public class CatchBTAActivity extends AppCompatActivity {

    private TextView tvDocNumber, tvDestination, tvType,
            tvTruckCode, tvTtlWeight, tvTtlQty, tvTtlRecord, tvWorkerListStr;
    private SQLiteDatabase db;
    private RecyclerView rv;
    private long catch_bta_id;
    private CatchBTAAdapter adapter;
    private boolean is_upload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_bta);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        tvDocNumber = findViewById(R.id.catch_bta_tv_doc_number);
        tvDestination = findViewById(R.id.catch_bta_tv_destination);
        tvType = findViewById(R.id.catch_bta_tv_type);
        tvTruckCode = findViewById(R.id.catch_bta_tv_truck_code);
        tvWorkerListStr = findViewById(R.id.catch_bta_tv_worker_list_str);

        tvTtlWeight = findViewById(R.id.catch_bta_tv_ttl_weight);
        tvTtlQty = findViewById(R.id.catch_bta_tv_ttl_qty);
        tvTtlRecord = findViewById(R.id.catch_bta_tv_ttl_record);
        setupStartIntent();
        setupSummary();
        setupRecycleView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRecycleView();
        setupTtlSummary();
    }

    private void setupStartIntent() {
        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_CATCH_BTA)) {
            catch_bta_id = intentStart.getLongExtra(I_KEY_CATCH_BTA, 0);
        }
    }

    private void setupSummary() {
        Cursor cursor = CatchBTAController.getById(db, catch_bta_id);
        cursor.moveToFirst();
        String doc_number = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_NUMBER));
        String doc_type = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_TYPE));
        String type = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TYPE));
        String truck_code = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TRUCK_CODE));
        int upload = cursor.getInt(cursor.getColumnIndex(CatchBTAEntry.COLUMN_UPLOAD));
        if (upload == 1) {
            is_upload = true;
        }

        if (type.equals("B")) {
            type = "C";
        }

        String destination = getString(R.string.bta_customer);
        if (doc_type.equals("IFT")) {
            destination = getString(R.string.bta_slaughterhouse);
        } else if (doc_type.equals("OP")) {
            destination = getString(R.string.bta_op);
        }

        tvDocNumber.setText("Doc Number : " + doc_number);
        tvDestination.setText("Destination : " + destination);
        tvType.setText("Type : " + type);
        tvTruckCode.setText("Truck Code : " + truck_code);

        String title = "Catch BTA";
        if (is_upload) {
            title += " (Uploaded)";
        }

        setTitle(title);

        Cursor cursorWorker = CatchBTAWorkerController.getAllByCatchBTAId(db, catch_bta_id);
        List<String> workerNameList = new ArrayList<String>();
        while (cursorWorker.moveToNext()) {
            String workerName = cursorWorker.getString(cursorWorker.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_WORKER_NAME));
            workerNameList.add(workerName);
        }

        if (workerNameList.size() == 0) {
            tvWorkerListStr.setText("Workers : " + getString(R.string.no_worker2));
        } else {
            StringBuilder str = new StringBuilder("");
            for (String wn : workerNameList) {
                str.append(wn).append(", ");
            }
            String wnStr = str.toString();
            wnStr = wnStr.substring(0, wnStr.length() - 2);
            tvWorkerListStr.setText("Workers : " + wnStr);
        }
    }

    public void setupTtlSummary() {
        int ttlQty = CatchBTADetailController.getTotalQtyByCatchBTAId(db, catch_bta_id);
        double ttlWeight = CatchBTADetailController.getTotalWeightByCatchBTAId(db, catch_bta_id);
        int ttlCage = CatchBTADetailController.getAllByCatchBTAId(db, catch_bta_id).getCount();

        tvTtlQty.setText("Total Quantity : " + ttlQty);
        tvTtlWeight.setText("Total Weight : " + String.format("%.2f", ttlWeight) + " Kg");
        tvTtlRecord.setText("Total Record : " + ttlCage + "");

    }

    private void setupRecycleView() {
        rv = this.findViewById(R.id.catch_bta_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = CatchBTADetailController.getAllByCatchBTAId(db, catch_bta_id);

        adapter = new CatchBTAAdapter(this, cursor);
        rv.setAdapter(adapter);
    }

    private void refreshRecycleView() {
        adapter.swapCursor(CatchBTADetailController.getAllByCatchBTAId(db, catch_bta_id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catch_bta_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_catch_bta_reupload) {
            if (!is_upload) {

                UIUtils.getMessageDialog(CatchBTAActivity.this, "Reupload Failed", "Not yet upload data is unable to mark reupload").show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(CatchBTAActivity.this).create();
                alertDialog.setTitle("Reupload this Catch BTA?");
                alertDialog.setMessage("Reupload will require to upload again");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "REUPLOAD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CatchBTAController.reupload(db, catch_bta_id);
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            }
        }
        if (id == R.id.action_catch_bta_print) {
            if (is_upload) {

                //UIUtils.getMessageDialog(CatchBTAActivity.this, "Print Failed", "Uploaded data is unable to print").show();

                AlertDialog alertDialog = new AlertDialog.Builder(CatchBTAActivity.this).create();
                alertDialog.setTitle("Uploaded data need enter password to print");

                final EditText etPassword = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                etPassword.setLayoutParams(lp);
                alertDialog.setView(etPassword);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PRINT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (etPassword.getText().toString().equals("8833")) {
                                    dialog.dismiss();
                                    printReceipt();
                                }else{
                                    UIUtils.getMessageDialog(CatchBTAActivity.this, "Unable to print", "Wrong password").show();
                                    dialog.dismiss();
                                }
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            } else {
                printReceipt();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void printReceipt(){
        String printText = PrintUtils.printCatchBTA(this, db, catch_bta_id);
        String qr = CatchBTAController.toQrData(db, catch_bta_id);

        Intent ppIntent = new Intent(CatchBTAActivity.this, PrintPreview2Activity.class);
        ppIntent.putExtra(I_KEY_PRINT_TEXT, printText);
        ppIntent.putExtra(I_KEY_PRINT_QR_TEXT, qr);
        ppIntent.putExtra(I_KEY_MODULE, MODULE_CATCH_BTA);
        ppIntent.putExtra(I_KEY_ID, catch_bta_id);
        startActivity(ppIntent);
    }

}
