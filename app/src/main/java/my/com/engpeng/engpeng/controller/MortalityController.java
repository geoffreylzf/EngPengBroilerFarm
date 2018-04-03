package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract.*;
import my.com.engpeng.engpeng.utilities.EPDateUtils;

/**
 * Created by Admin on 6/1/2018.
 */

public class MortalityController {
    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           int house_code,
                           String record_date,
                           int m_q,
                           int r_q) {

        ContentValues cv = new ContentValues();
        cv.put(MortalityEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(MortalityEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(MortalityEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(MortalityEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(MortalityEntry.COLUMN_M_Q, m_q);
        cv.put(MortalityEntry.COLUMN_R_Q, r_q);
        return db.insert(MortalityEntry.TABLE_NAME, null, cv);
    }

    public static int check(SQLiteDatabase db,
                            int company_id,
                            int location_id,
                            int house_code,
                            String record_date) {

        String selection = MortalityEntry.COLUMN_COMPANY_ID + " = ? AND " +
                MortalityEntry.COLUMN_LOCATION_ID + " = ? AND " +
                MortalityEntry.COLUMN_HOUSE_CODE + " = ? AND " +
                MortalityEntry.COLUMN_RECORD_DATE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
                record_date,
        };

        return db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry._ID
        ).getCount();
    }

    public static Cursor getAllByCLHU(SQLiteDatabase db,
                                      int company_id,
                                      int location_id,
                                      int house_code) {

        String selection = MortalityEntry.COLUMN_COMPANY_ID + " = ? AND " +
                MortalityEntry.COLUMN_LOCATION_ID + " = ? AND " +
                MortalityEntry.COLUMN_HOUSE_CODE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        return db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        );
    }

    public static String getLastDayByCLHU(SQLiteDatabase db,
                                          int company_id,
                                          int location_id,
                                          int house_code) {

        String[] columns = new String[]{
                "MAX(" + MortalityEntry.COLUMN_RECORD_DATE + ") AS " + MortalityEntry.COLUMN_RECORD_DATE,
        };

        String selection = MortalityEntry.COLUMN_COMPANY_ID + " = ? AND " +
                MortalityEntry.COLUMN_LOCATION_ID + " = ? AND " +
                MortalityEntry.COLUMN_HOUSE_CODE + " = ? " ;

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        Cursor cursor = db.query(
                MortalityEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String msg = "No Record";

        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_RECORD_DATE));
        if (date != null) {
            msg = EPDateUtils.getDateDiffDesc(msg, date);
        }
        return msg;
    }

    public static String getLastMRByCLHU(SQLiteDatabase db,
                                          int company_id,
                                          int location_id,
                                          int house_code) {

        String selection = MortalityEntry.COLUMN_COMPANY_ID + " = ? AND " +
                MortalityEntry.COLUMN_LOCATION_ID + " = ? AND " +
                MortalityEntry.COLUMN_HOUSE_CODE + " = ? " ;

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        Cursor cursor = db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String msg = "";

        if(cursor.moveToFirst()) {
            if(EPDateUtils.isToday(cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_RECORD_DATE)))){
                String mortal = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_M_Q));
                String reject = cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_R_Q));
                msg = "   -   M: " + mortal + "   R: " + reject;

                if (cursor.getInt(cursor.getColumnIndex(MortalityEntry.COLUMN_UPLOAD)) == 1){
                    msg += "   -   Uploaded";
                }
            }
        }
        return msg;
    }

    public static Cursor getById(SQLiteDatabase db, long id) {

        String selection = MortalityEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        return db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(MortalityEntry.TABLE_NAME, MortalityEntry._ID + "=" + id, null) > 0;
    }

    public static boolean removeUploaded(SQLiteDatabase db) {
        return db.delete(MortalityEntry.TABLE_NAME, MortalityEntry.COLUMN_UPLOAD + "=" + 1, null) > 0;
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = MortalityEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = MortalityEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                MortalityEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                MortalityEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + MortalityEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + MortalityEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + MortalityEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + MortalityEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + MortalityEntry.COLUMN_RECORD_DATE + "\":\" " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + MortalityEntry.COLUMN_M_Q + "\": " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_M_Q)) + ",";
            json += "\"" + MortalityEntry.COLUMN_R_Q + "\": " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_R_Q)) + ",";
            json += "\"" + MortalityEntry.COLUMN_TIMESTAMP + "\":\" " + cursor.getString(cursor.getColumnIndex(MortalityEntry.COLUMN_TIMESTAMP)) + "\"";
            if (cursor.getPosition() == (cursor.getCount() - 1)) {
                json += "}";
            } else {
                json += "},";
            }
        }
        json += "]}";
        return json;
    }
}
