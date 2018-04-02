package my.com.engpeng.engpeng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static my.com.engpeng.engpeng.Global.I_KEY_ID;
import static my.com.engpeng.engpeng.Global.I_KEY_MODULE;
import static my.com.engpeng.engpeng.Global.I_KEY_PRINT_TEXT;

public class PrintPreviewActivity extends AppCompatActivity {
    private TextView tvText;
    private Button btnPrint;
    private String printText, module;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);

        tvText = findViewById(R.id.print_preview_tv_text);
        btnPrint = findViewById(R.id.print_preview_btn_print);

        Intent intentStart = getIntent();
        if (intentStart.hasExtra(I_KEY_PRINT_TEXT)) {
            printText = intentStart.getStringExtra(I_KEY_PRINT_TEXT);
        }
        if (intentStart.hasExtra(I_KEY_MODULE)) {
            module = intentStart.getStringExtra(I_KEY_MODULE);
        }
        if (intentStart.hasExtra(I_KEY_ID)) {
            id = intentStart.getLongExtra(I_KEY_ID, 0);
        }

        tvText.setText(printText);
        setupListener();

        setTitle("Print Preview");
    }

    private void setupListener(){
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btIntent = new Intent(PrintPreviewActivity.this, BluetoothActivity.class);
                btIntent.putExtra(I_KEY_PRINT_TEXT, printText);
                btIntent.putExtra(I_KEY_MODULE, module);
                btIntent.putExtra(I_KEY_ID, id);
                startActivity(btIntent);
            }
        });
    }
}
