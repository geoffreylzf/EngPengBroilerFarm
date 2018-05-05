package my.com.engpeng.engpeng;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import my.com.engpeng.engpeng.data.EngPengDbHelper;

public class TempFeedTransferDetailActivity extends AppCompatActivity {

    private Button btnStart;
    private RecyclerView rv;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_feed_transfer_detail);

        btnStart = this.findViewById(R.id.temp_feed_transfer_detail_btn_save);
        rv = this.findViewById(R.id.temp_feed_transfer_detail_rv);

        EngPengDbHelper dbHelper = new EngPengDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        setTitle("Select Multiple Item (Pilih Baja Yang Dihantar)");

        //setupRecycleView();
    }
}
