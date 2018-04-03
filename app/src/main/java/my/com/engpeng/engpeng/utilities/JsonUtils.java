package my.com.engpeng.engpeng.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 18/1/2018.
 */

public class JsonUtils {

    private static final String MESSAGE_CODE = "cod";
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

    private static final String MORTALITY = "mobile_mortality";
    private static final String RECORD_DATE = "record_date";
    private static final String M_Q = "m_q";
    private static final String R_Q = "r_q";
    private static final String TIMESTAMP = "timestamp";

    public static boolean getAuthentication(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has(MESSAGE_CODE)) {
                int errorCode = json.getInt(MESSAGE_CODE);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return true;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        return false;
                    default:
                        return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
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
            JSONArray jsonArray = json.getJSONArray(MORTALITY);
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
}
