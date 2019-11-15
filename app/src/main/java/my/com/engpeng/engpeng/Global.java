package my.com.engpeng.engpeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.data.EngPengContract;
import my.com.engpeng.engpeng.utilities.ReminderUtils;

/**
 * Created by Admin on 5/1/2018.
 */

public class Global {

    public static int sCompanyId;
    public static String sCompanyCode;
    public static String sCompanyName;

    public static int sLocationId;
    public static String sLocationCode;
    public static String sLocationName;

    public static String sUsername;
    public static String sPassword;

    public static final int REQUEST_CODE_BLUETOOTH_WEIGHT = 7001;
    public static final int REQUEST_CODE_BLUETOOTH_DEVICE = 7002;
    public static final int REQUEST_CODE_WRITE_EXTERNAL = 7003;
    public static final int REQUEST_CODE_BARCODE_CAPTURE = 9001;

    public static final String PREF_KEY = "PREF_KEY";
    public static final String MODULE_MORTALITY = "MORTALITY";
    public static final String MODULE_CATCH_BTA = "CATCH_BTA";
    public static final String MODULE_WEIGHT = "WEIGHT";

    //Intent param keys
    public static final String I_KEY_MODULE = "I_KEY_MODULE";
    public static final String I_KEY_HOUSE_CODE = "I_KEY_HOUSE_CODE";
    public static final String I_KEY_COMPANY = "I_KEY_COMPANY";
    public static final String I_KEY_LOCATION = "I_KEY_LOCATION";
    public static final String I_KEY_SECTION = "I_KEY_SECTION";
    public static final String I_KEY_WEIGHT = "I_KEY_WEIGHT";
    public static final String I_KEY_CATCH_BTA = "I_KEY_CATCH_BTA";
    public static final String I_KEY_PRINT_TEXT = "I_KEY_PRINT_TEXT";
    public static final String I_KEY_PRINT_QR_TEXT = "I_KEY_PRINT_QR_TEXT";
    public static final String I_KEY_PRINT_QR_TOP = "I_KEY_PRINT_QR_TOP";
    public static final String I_KEY_PRINT_QR_BOTTOM = "I_KEY_PRINT_QR_BOTTOM";
    public static final String I_KEY_DIRECT_RUN = "I_KEY_DIRECT_RUN";
    public static final String I_KEY_QTY = "I_KEY_QTY";
    public static final String I_KEY_CAGE_QTY = "I_KEY_CAGE_QTY";
    public static final String I_KEY_WITH_COVER_QTY = "I_KEY_WITH_COVER_QTY";
    public static final String I_KEY_CONTINUE_NEXT = "I_KEY_CONTINUE_NEXT";
    public static final String I_KEY_ID = "I_KEY_ID";
    public static final String I_KEY_DOC_ID = "I_KEY_DOC_ID";
    public static final String I_KEY_DOC_NUMBER = "I_KEY_DOC_NUMBER";
    public static final String I_KEY_DOC_TYPE = "I_KEY_DOC_TYPE";
    public static final String I_KEY_TYPE = "I_KEY_TYPE";
    public static final String I_KEY_TRUCK_CODE = "I_KEY_TRUCK_CODE";
    public static final String I_KEY_FASTING_TIME = "I_KEY_FASTING_TIME";
    public static final String I_KEY_RECORD_DATE = "I_KEY_RECORD_DATE";
    public static final String I_KEY_ID_LIST = "I_KEY_ID_LIST";
    public static final String I_KEY_QR_DATA = "I_KEY_QR_DATA";
    public static final String I_KEY_DISCHARGE_HOUSE = "I_KEY_DISCHARGE_HOUSE";
    public static final String I_KEY_RECEIVE_HOUSE = "I_KEY_RECEIVE_HOUSE";
    public static final String I_KEY_DISCHARGE_CODE = "I_KEY_DISCHARGE_CODE";
    public static final String I_KEY_RUNNING_NO = "I_KEY_RUNNING_NO";
    public static final String I_KEY_NETT_VALUE = "I_KEY_NETT_VALUE";
    public static final String I_KEY_BLUETOOTH_NAME = "I_KEY_BLUETOOTH_NAME";
    public static final String I_KEY_BLUETOOTH_ADDRESS = "I_KEY_BLUETOOTH_ADDRESS";

    //Preferences param keys
    public static final String P_KEY_USERNAME = "P_KEY_USERNAME";
    public static final String P_KEY_PASSWORD = "P_KEY_PASSWORD";
    public static final String P_KEY_COMPANY_ID = "P_KEY_COMPANY_ID";
    public static final String P_KEY_LOCATION_ID = "P_KEY_LOCATION_ID";
    public static final String P_KEY_BLUETOOTH_NAME = "P_KEY_BLUETOOTH_NAME";
    public static final String P_KEY_BLUETOOTH_ADDRESS = "P_KEY_BLUETOOTH_ADDRESS";
    public static final String P_KEY_PRINTER_BLUETOOTH_NAME = "P_KEY_PRINTER_BLUETOOTH_NAME";
    public static final String P_KEY_PRINTER_BLUETOOTH_ADDRESS = "P_KEY_PRINTER_BLUETOOTH_ADDRESS";

    //Assign different id for different loader
    public static final int UPLOAD_LOADER_ID = 1001;
    public static final int LOCATION_INFO_LOADER_ID = 1002;
    public static final int LOGIN_LOADER_ID = 1003;
    public static final int LOG_LOADER_ID = 1004;
    public static final int PRINT_PREVIEW_LOADER_ID = 1005;

    //item_uom_code
    public static final String UOM_CODE_BG = "Bg";
    public static final String UOM_CODE_MT = "MT";

    //qr_line_type
    public static final String QR_SPLIT_LINE = "\\r?\\n";
    public static final String QR_SPLIT_FIELD = "\\|";
    public static final String QR_LINE_TYPE_HEAD = "H";
    public static final String QR_LINE_TYPE_DETAIL = "D";
    public static final String QR_LINE_TYPE_COMPARTMENT = "C";

    //running_code
    public static final String RUNNING_CODE_DISCHARGE = "D";
    public static final String RUNNING_CODE_RECEIVE = "R";
    public static final String RUNNING_CODE_TRANSFER = "T";

    //bluetooth weighing
    public static final String BT_WT_PREFIX_KG = "kg";

    public static void setupGlobalVariables(Context context, SQLiteDatabase db) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if (prefs.contains(P_KEY_USERNAME) && prefs.contains(P_KEY_PASSWORD)) {
            sUsername = prefs.getString(P_KEY_USERNAME, "");
            sPassword = prefs.getString(P_KEY_PASSWORD, "");
            //ReminderUtils.scheduleReminder(context);
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }

        if (prefs.contains(P_KEY_COMPANY_ID) && prefs.contains(P_KEY_LOCATION_ID)) {
            sCompanyId = prefs.getInt(P_KEY_COMPANY_ID, 0);
            sLocationId = prefs.getInt(P_KEY_LOCATION_ID, 0);

            if (sCompanyId == 0 || sLocationId == 0) {
                setEmptyCompanyLocation();

            } else {
                Cursor companyCursor = BranchController.getBranchByErpId(db, sCompanyId);
                Cursor locationCursor = BranchController.getBranchByErpId(db, sLocationId);
                if (companyCursor.moveToFirst() && locationCursor.moveToFirst()) {
                    sCompanyCode = companyCursor.getString(companyCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
                    sCompanyName = companyCursor.getString(companyCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

                    sLocationCode = locationCursor.getString(locationCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
                    sLocationName = locationCursor.getString(locationCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));
                } else {
                    setEmptyCompanyLocation();
                }
            }

        } else {
            setEmptyCompanyLocation();
        }
    }

    public static void setEmptyCompanyLocation() {
        sCompanyId = 0;
        sLocationId = 0;

        sLocationCode = "Non";
        sLocationName = "No Location Selected";

        sCompanyCode = "Non";
        sCompanyName = "No Company Selected";
    }
}
