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
    public static final String I_KEY_DIRECT_RUN = "I_KEY_DIRECT_RUN";
    public static final String I_KEY_QTY = "I_KEY_QTY";
    public static final String I_KEY_CAGE_QTY = "I_KEY_CAGE_QTY";
    public static final String I_KEY_WITH_COVER_QTY = "I_KEY_WITH_COVER_QTY";
    public static final String I_KEY_CONTINUE_NEXT = "I_KEY_CONTINUE_NEXT";
    public static final String I_KEY_ID = "I_KEY_ID";
    public static final String I_KEY_DOC_NUMBER = "I_KEY_DOC_NUMBER";
    public static final String I_KEY_TYPE = "I_KEY_TYPE";
    public static final String I_KEY_TRUCK_CODE = "I_KEY_TRUCK_CODE";
    public static final String I_KEY_RECORD_DATE = "I_KEY_RECORD_DATE";
    public static final String I_KEY_ID_LIST = "I_KEY_ID_LIST";

    //Preferences param keys
    public static final String P_KEY_USERNAME = "P_KEY_USERNAME";
    public static final String P_KEY_PASSWORD = "P_KEY_PASSWORD";
    public static final String P_KEY_COMPANY_ID = "P_KEY_COMPANY_ID";
    public static final String P_KEY_LOCATION_ID = "P_KEY_LOCATION_ID";

    //Assign different id for different loader
    public static final int UPLOAD_LOADER_ID = 1001;
    public static final int LOCATION_INFO_LOADER_ID = 1002;
    public static final int LOGIN_LOADER_ID = 1003;
    public static final int LOG_LOADER_ID = 1004;

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
                if (companyCursor.moveToFirst() && locationCursor.moveToFirst() ) {
                    sCompanyCode = companyCursor.getString(companyCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
                    sCompanyName = companyCursor.getString(companyCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

                    sLocationCode = locationCursor.getString(locationCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
                    sLocationName = locationCursor.getString(locationCursor.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));
                }else{
                    setEmptyCompanyLocation();
                }
            }

        } else {
            setEmptyCompanyLocation();
        }
    }

    public static void setEmptyCompanyLocation(){
        sCompanyId = 0;
        sLocationId = 0;

        sLocationCode = "Non";
        sLocationName = "No Location Selected";

        sCompanyCode = "Non";
        sCompanyName = "No Company Selected";
    }
}
