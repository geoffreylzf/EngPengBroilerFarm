package my.com.engpeng.engpeng.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.WeightDetailController;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 18/1/2018.
 */

public class JsonUtils {

    private static final String COD = "cod";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String ROW = "row";

    private static final String BRANCH = "branch";
    private static final String ID = "id";
    private static final String BRANCH_CODE = "branch_code";
    private static final String BRANCH_NAME = "branch_name";
    private static final String COMPANY_ID = "company_id";

    private static final String HOUSE = "house";
    private static final String LOCATION_ID = "location_id";
    private static final String HOUSE_CODE = "house_code";

    private static final String MOBILE_MORTALITY = "mobile_mortality";
    private static final String RECORD_DATE = "record_date";
    private static final String M_Q = "m_q";
    private static final String R_Q = "r_q";
    private static final String TIMESTAMP = "timestamp";

    private static final String TYPE = "type";
    private static final String DAY = "day";

    private static final String STANDARD_WEIGHT = "standard_weight";
    private static final String AVG_WEIGHT = "avg_weight";

    private static final String FEED_ITEM = "feed_item";
    private static final String SKU_CODE = "sku_code";
    private static final String SKU_NAME = "sku_name";
    private static final String ITEM_UOM_ID = "item_uom_id";


    public static boolean getAuthentication(Context context, String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(COD)) {
                int errorCode = json.getInt(COD);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return true;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        UIUtils.showToastMessage(context, "The request URL was not found");
                        return false;
                    case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                        UIUtils.showToastMessage(context, "Insufficient data to login");
                        return false;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        UIUtils.showToastMessage(context, "Unauthorized (Please check username or password)");
                        return false;
                    default:
                        UIUtils.showToastMessage(context, "Unknown error");
                        return false;
                }
            }
            UIUtils.showToastMessage(context, "Error (no respond)");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showToastMessage(context, "Error (" + e.getMessage() + ")");
            return false;
        }
    }

    public static boolean getLoginAuthentication(Context context, String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(SUCCESS)) {
                boolean success = json.getBoolean(SUCCESS);
                if(success){
                    return true;
                }else{
                    String message = json.getString(MESSAGE);
                    UIUtils.showToastMessage(context, "Error ("+message+")");
                }
            }else {
                UIUtils.showToastMessage(context, "Login Authentication Error (no respond)");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showToastMessage(context, "Login Authentication Error (" + e.getMessage() + ")");
            return false;
        }
    }

    public static ContentValues[] getBranchContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(BRANCH);
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                int id, company_id;
                String branch_code, branch_name;
                JSONObject branch = jsonArray.getJSONObject(i);

                id = branch.getInt(ID);
                branch_code = branch.getString(BRANCH_CODE);
                branch_name = branch.getString(BRANCH_NAME);
                company_id = branch.getInt(COMPANY_ID);

                ContentValues cv = new ContentValues();
                cv.put(BranchEntry.COLUMN_ERP_ID, id);
                cv.put(BranchEntry.COLUMN_BRANCH_CODE, branch_code);
                cv.put(BranchEntry.COLUMN_BRANCH_NAME, branch_name);
                cv.put(BranchEntry.COLUMN_COMPANY_ID, company_id);
                cvs[i] = cv;
            }
            return cvs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ContentValues[] getHouseContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(HOUSE);
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                int location_id, house_code;
                JSONObject house = jsonArray.getJSONObject(i);

                location_id = house.getInt(LOCATION_ID);
                house_code = house.getInt(HOUSE_CODE);

                ContentValues cv = new ContentValues();
                cv.put(HouseEntry.COLUMN_LOCATION_ID, location_id);
                cv.put(HouseEntry.COLUMN_HOUSE_CODE, house_code);
                cvs[i] = cv;
            }
            return cvs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ContentValues[] getMortalityContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(MOBILE_MORTALITY);
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mortality = jsonArray.getJSONObject(i);
                ContentValues cv = new ContentValues();

                cv.put(MortalityEntry.COLUMN_COMPANY_ID, mortality.getInt(COMPANY_ID));
                cv.put(MortalityEntry.COLUMN_HOUSE_CODE, mortality.getInt(HOUSE_CODE));
                cv.put(MortalityEntry.COLUMN_LOCATION_ID, mortality.getInt(LOCATION_ID));
                cv.put(MortalityEntry.COLUMN_M_Q, mortality.getInt(M_Q));
                cv.put(MortalityEntry.COLUMN_R_Q, mortality.getInt(R_Q));
                cv.put(MortalityEntry.COLUMN_RECORD_DATE, mortality.getString(RECORD_DATE));
                cv.put(MortalityEntry.COLUMN_TIMESTAMP, mortality.getString(TIMESTAMP));
                cv.put(MortalityEntry.COLUMN_UPLOAD, 1);

                cvs[i] = cv;
            }
            return cvs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ContentValues[] getStandardWeightContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(STANDARD_WEIGHT);
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject sw = jsonArray.getJSONObject(i);
                ContentValues cv = new ContentValues();

                cv.put(StandardWeightEntry.COLUMN_TYPE, sw.getString(TYPE));
                cv.put(StandardWeightEntry.COLUMN_DAY, sw.getInt(DAY));
                cv.put(StandardWeightEntry.COLUMN_AVG_WEIGHT, sw.getInt(AVG_WEIGHT));

                cvs[i] = cv;
            }
            return cvs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ContentValues[] getFeedItemContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray(FEED_ITEM);
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject fi = jsonArray.getJSONObject(i);
                ContentValues cv = new ContentValues();

                cv.put(FeedItemEntry.COLUMN_ERP_ID, fi.getInt(ID));
                cv.put(FeedItemEntry.COLUMN_SKU_CODE, fi.getString(SKU_CODE));
                cv.put(FeedItemEntry.COLUMN_SKU_NAME, fi.getString(SKU_NAME));
                cv.put(FeedItemEntry.COLUMN_ITEM_UOM_ID, fi.getInt(ITEM_UOM_ID));

                cvs[i] = cv;
            }
            return cvs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getStatus(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(STATUS)) {
                int status = json.getInt(STATUS);

                if (status == 1) {
                    return true;
                } else if (status == 0) {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUploadRow(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(ROW)) {
                int row = json.getInt(ROW);
                return row;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static ContentValues[] getPersonStaffContentValues(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray jsonArray = json.getJSONArray("person_staff");
            ContentValues[] cvs = new ContentValues[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                int id;
                String personCode, personName;
                JSONObject ps = jsonArray.getJSONObject(i);

                id = ps.getInt(ID);
                personCode = ps.getString("person_code");
                personName = ps.getString("person_name");

                ContentValues cv = new ContentValues();
                cv.put(PersonStaffEntry._ID, id);
                cv.put(PersonStaffEntry.COLUMN_PERSON_CODE, personCode);
                cv.put(PersonStaffEntry.COLUMN_PERSON_NAME, personName);
                cvs[i] = cv;
            }
            return cvs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
