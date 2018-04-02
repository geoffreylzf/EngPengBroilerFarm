package my.com.engpeng.engpeng;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import my.com.engpeng.engpeng.adapter.CompanyListAdapter;
import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.data.EngPengDbHelper;

public class CompanyListActivity extends AppCompatActivity {

    private CompanyListAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        RecyclerView companyListRecyclerView = this.findViewById(R.id.company_list_view);
        companyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = BranchController.getAllCompany(mDb);

        mAdapter = new CompanyListAdapter(this, cursor);
        companyListRecyclerView.setAdapter(mAdapter);

        setTitle("Select Company");

    }
}
