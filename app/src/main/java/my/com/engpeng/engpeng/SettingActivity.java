package my.com.engpeng.engpeng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.com.engpeng.engpeng.data.EngPengDbHelper;
import my.com.engpeng.engpeng.utilities.UIUtils;

import static my.com.engpeng.engpeng.Global.REQUEST_CODE_WRITE_EXTERNAL;

public class SettingActivity extends AppCompatActivity {

    private Button btnBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnBackup = findViewById(R.id.btn_setting);

        setTitle("Setting");

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkWritePermission()) {
                    if (backupDatabase()) {
                        UIUtils.getMessageDialog(SettingActivity.this, "Success", "Backup success, please check your folder").show();

                    } else {
                        UIUtils.getMessageDialog(SettingActivity.this, "Error", "Backup failed").show();
                    }
                } else {
                    requestWritePermission();
                }
            }
        });
    }

    public boolean checkWritePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestWritePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                REQUEST_CODE_WRITE_EXTERNAL);
    }


    private boolean backupDatabase() {

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        Date currentTime = Calendar.getInstance().getTime();


        String packageName = this.getPackageName();
        String dbPath = "/data/data/" + packageName + "/databases/" + EngPengDbHelper.DATABASE_NAME;
        String backupPath = "/sdcard/" +
                df.format(currentTime) + "_" + EngPengDbHelper.DATABASE_NAME;

        try {

            FileInputStream fis = new FileInputStream(new File(dbPath));
            FileOutputStream fos = new FileOutputStream(new File(backupPath));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            fis.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
