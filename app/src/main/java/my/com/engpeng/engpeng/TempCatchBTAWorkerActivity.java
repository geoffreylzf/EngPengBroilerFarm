package my.com.engpeng.engpeng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import my.com.engpeng.engpeng.adapter.TempCatchBTAWorkerSelectionAdapter;
import my.com.engpeng.engpeng.adapter.TempCatchBTAWorkerTempAdapter;
import my.com.engpeng.engpeng.controller.PersonStaffController;
import my.com.engpeng.engpeng.controller.TempCatchBTAWorkerController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.loader.PersonStaffAsyncTaskLoader;
import my.com.engpeng.engpeng.model.PersonStaffSelection;
import my.com.engpeng.engpeng.model.TempBtaWorker;
import my.com.engpeng.engpeng.utilities.DatabaseUtils;
import my.com.engpeng.engpeng.utilities.JsonUtils;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.sPassword;
import static my.com.engpeng.engpeng.Global.sUsername;

public class TempCatchBTAWorkerActivity
        extends AppCompatActivity
        implements
        TempCatchBTAWorkerSelectionAdapter.TempCatchBTAWorkerSelectionAdapterListener,
        AppLoader.AppLoaderListener,
        PersonStaffAsyncTaskLoader.PersonStaffAsyncTaskLoaderListener {

    private EditText etFilter, etManualWorker;
    private Button btnRefresh, btnAddManualWorker;
    private RecyclerView rvSelection, rvTemp;
    private TempCatchBTAWorkerSelectionAdapter selectionAdapter;
    private TempCatchBTAWorkerTempAdapter tempAdapter;

    private SQLiteDatabase db;

    private final List<PersonStaffSelection> mPersonStaffSelectionList = new ArrayList<PersonStaffSelection>();
    private final List<TempBtaWorker> mTempBtaWorkerList = new ArrayList<TempBtaWorker>();

    private AppLoader loader;
    private Dialog progressDialog;
    private CheckBox cbLocal;
    private boolean isFetching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_catch_bta_worker);
        setTitle("New Catch BTA Workers");

        etFilter = findViewById(R.id.temp_catch_bta_worker_et_filter);
        rvSelection = findViewById(R.id.temp_catch_bta_worker_rv_selection);
        btnRefresh = findViewById(R.id.temp_catch_bta_worker_btn_refresh);
        etManualWorker = findViewById(R.id.temp_catch_bta_worker_et_manual);
        btnAddManualWorker = findViewById(R.id.temp_catch_bta_worker_btn_manual);
        rvTemp = findViewById(R.id.temp_catch_bta_worker_rv_temp);
        cbLocal = findViewById(R.id.temp_catch_bta_worker_cb_local);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        db = dbHelper.getWritableDatabase();

        progressDialog = UIUtils.getProgressDialog(this);
        loader = new AppLoader(this);
        getSupportLoaderManager().initLoader(Global.PERSON_STAFF_LOADER_ID, null, loader);

        this.setupRv();
        this.setupListener();
    }

    @Override
    public void onBackPressed() {
        if (!isFetching) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void setupListener() {
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFetching) {
                    fetchPersonStaffData();
                }
            }
        });
        btnAddManualWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addManualWorker();
            }
        });
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshSelectionList();
            }
        });
    }

    private void setupRv() {
        rvSelection.setLayoutManager(new LinearLayoutManager(this));
        selectionAdapter = new TempCatchBTAWorkerSelectionAdapter(
                this,
                mPersonStaffSelectionList,
                this);
        rvSelection.setAdapter(selectionAdapter);

        rvTemp.setLayoutManager(new LinearLayoutManager(this));
        tempAdapter = new TempCatchBTAWorkerTempAdapter(
                this,
                mTempBtaWorkerList);
        rvTemp.setAdapter(tempAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int id = (int) viewHolder.itemView.getTag();

                Cursor cw = TempCatchBTAWorkerController.getById(db, id);
                cw.moveToFirst();
                String workerName = cw.getString(cw.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_WORKER_NAME));

                AlertDialog alertDialog = new AlertDialog.Builder(TempCatchBTAWorkerActivity.this).create();
                alertDialog.setTitle("Delete Worker ?");
                String message = "Worker : " + workerName + "\n\n";

                alertDialog.setMessage(message);
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE(BUANG)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TempCatchBTAWorkerController.remove(db, id);
                                refreshSelectionList();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL(BALIK)",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshSelectionList();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }).attachToRecyclerView(rvTemp);

        refreshSelectionList();
    }

    @Override
    public void onClick(int psId, String workerName) {
        Cursor c = TempCatchBTAWorkerController.getByPersonStaffId(db, psId);
        if (c.getCount() > 0) {
            TempCatchBTAWorkerController.removeByPersonStaffId(db, psId);
        } else {
            TempCatchBTAWorkerController.add(db, psId, workerName);
        }
        refreshSelectionList();
    }

    private void addManualWorker() {
        String workerName = etManualWorker.getText().toString();
        if (workerName.length() == 0) {
            UIUtils.getMessageDialog(
                    this,
                    "Error",
                    "Please valid name (Tolong masukkan nama betul").show();
            return;
        }
        TempCatchBTAWorkerController.add(db, null, workerName);
        etManualWorker.setText("");
        refreshSelectionList();
    }

    private void refreshPersonStaffList() {
        Cursor cursorPs = PersonStaffController.getAllByFilter(db, etFilter.getText().toString());
        mPersonStaffSelectionList.clear();
        while (cursorPs.moveToNext()) {
            int psId = cursorPs.getInt(cursorPs.getColumnIndex(EngPengContract.PersonStaffEntry._ID));
            String code = cursorPs.getString(cursorPs.getColumnIndex(EngPengContract.PersonStaffEntry.COLUMN_PERSON_CODE));
            String name = cursorPs.getString(cursorPs.getColumnIndex(EngPengContract.PersonStaffEntry.COLUMN_PERSON_NAME));

            PersonStaffSelection ps = new PersonStaffSelection(psId, code, name);
            mPersonStaffSelectionList.add(ps);
        }
    }

    private void refreshTempList() {
        Cursor cursorTemp = TempCatchBTAWorkerController.getAll(db);
        mTempBtaWorkerList.clear();
        while (cursorTemp.moveToNext()) {
            int tempId = cursorTemp.getInt(cursorTemp.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry._ID));
            String psIdStr = cursorTemp.getString(cursorTemp.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID));
            Integer psId = null;
            try {
                psId = Integer.valueOf(psIdStr);
            } catch (NumberFormatException ex) {
                //DO Nothing
            }

            String workerName = cursorTemp.getString(cursorTemp.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_WORKER_NAME));

            TempBtaWorker temp = new TempBtaWorker(tempId, psId, workerName);
            mTempBtaWorkerList.add(temp);
        }
    }

    private void refreshSelectionList() {
        this.refreshPersonStaffList();
        this.refreshTempList();

        for (PersonStaffSelection pss : mPersonStaffSelectionList) {
            boolean isSelect = false;
            for (TempBtaWorker temp : mTempBtaWorkerList) {
                if (temp.getPersonStaffId() != null
                        && pss.getId() == temp.getPersonStaffId()) {
                    isSelect = true;
                    break;
                }
            }
            pss.setSelect(isSelect);
        }
        selectionAdapter.setList(mPersonStaffSelectionList);
        tempAdapter.setList(mTempBtaWorkerList);
    }

    private void fetchPersonStaffData() {
        String username = sUsername;
        String password = sPassword;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(AppLoader.LOADER_EXTRA_USERNAME, username);
        queryBundle.putString(AppLoader.LOADER_EXTRA_PASSWORD, password);

        if (cbLocal.isChecked()) {
            queryBundle.putBoolean(AppLoader.LOADER_IS_LOCAL, true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> psLoader = loaderManager.getLoader(Global.PERSON_STAFF_LOADER_ID);

        if (psLoader == null) {
            loaderManager.initLoader(Global.PERSON_STAFF_LOADER_ID, queryBundle, loader);
        } else {
            loaderManager.restartLoader(Global.PERSON_STAFF_LOADER_ID, queryBundle, loader);
        }
    }

    @Override
    public void beforeLocationInfoAsyncTaskLoaderStart() {
        progressDialog.show();
    }

    @Override
    public Loader<String> getAsyncTaskLoader(Bundle args) {
        return new PersonStaffAsyncTaskLoader(this, args, this);
    }

    @Override
    public void afterLoaderDone(String json) {
        progressDialog.hide();
        if (json != null && !json.equals("")) {
            if (JsonUtils.getAuthentication(this, json)) {
                new TempCatchBTAWorkerActivity.InsertPersonStaffDataTask().execute(json);
            }
        } else {
            UIUtils.getMessageDialog(this, "Error", "Failed to get person staff").show();
        }
    }

    private class InsertPersonStaffDataTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            isFetching = true;
            String json = strings[0];
            System.out.println(json);
            ContentValues[] cvs = JsonUtils.getPersonStaffContentValues(json);
            if (cvs != null && cvs.length != 0) {
                PersonStaffController.deleteAll(db);
                DatabaseUtils.insertPersonStaff(db, cvs);
                TempCatchBTAWorkerController.deleteAll(db);

                TempCatchBTAWorkerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshSelectionList();
                    }
                });
            } else {
                TempCatchBTAWorkerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.getMessageDialog(TempCatchBTAWorkerActivity.this,
                                "Error",
                                "Not person staff retrieved!").show();
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            isFetching = false;
        }
    }


}