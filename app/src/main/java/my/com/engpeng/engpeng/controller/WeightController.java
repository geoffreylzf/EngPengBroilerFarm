package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.utilities.EPDateUtils;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 15/1/2018.
 */

public class WeightController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           int house_code,
                           int day,
                           String record_date,
                           String record_time,
                           String feed) {

        ContentValues cv = new ContentValues();
        cv.put(WeightEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(WeightEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(WeightEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(WeightEntry.COLUMN_DAY, day);
        cv.put(WeightEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(WeightEntry.COLUMN_RECORD_TIME, record_time);
        cv.put(WeightEntry.COLUMN_FEED, feed);
        return db.insert(WeightEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getById(SQLiteDatabase db, Long id) {
        String selection = WeightEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };
        return db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry.COLUMN_RECORD_DATE + " DESC"
        );
    }

    public static Cursor getAllByCLHU(SQLiteDatabase db,
                                      int company_id,
                                      int location_id,
                                      int house_code) {

        String selection = WeightEntry.COLUMN_COMPANY_ID + " = ? AND " +
                WeightEntry.COLUMN_LOCATION_ID + " = ? AND " +
                WeightEntry.COLUMN_HOUSE_CODE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        return db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry._ID + " DESC"
        );
    }

    public static Cursor getAllByDate(SQLiteDatabase db,
                                      String record_date) {

        String selection = WeightEntry.COLUMN_RECORD_DATE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(record_date),
        };

        return db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry._ID + " DESC"
        );
    }

    public static String getLastDayByCLHU(SQLiteDatabase db,
                                          int company_id,
                                          int location_id,
                                          int house_code) {

        String selection = WeightEntry.COLUMN_COMPANY_ID + " = ? AND " +
                WeightEntry.COLUMN_LOCATION_ID + " = ? AND " +
                WeightEntry.COLUMN_HOUSE_CODE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        Cursor cursor = db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String msg = "No Record";

        if(cursor.moveToFirst()){
            String date = cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_DATE));
            if (date != null) {
                msg = EPDateUtils.getDateDiffDesc(msg, date);
                if (cursor.getInt(cursor.getColumnIndex(WeightEntry.COLUMN_UPLOAD)) == 1){
                    msg += "   -   Uploaded";
                }
            }
        }

        return msg;
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(WeightEntry.TABLE_NAME, WeightEntry._ID + "=" + id, null) > 0;
    }

    public static void removeUploaded(SQLiteDatabase db) {
        String selection = WeightEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while(cursor.moveToNext()){
            long weight_id = cursor.getLong(cursor.getColumnIndex(WeightEntry._ID));
            remove(db, weight_id);
            WeightDetailController.removeByWeightId(db, weight_id);
        }
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = WeightEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = WeightEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                WeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + WeightEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + WeightEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + WeightEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + WeightEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + WeightEntry.COLUMN_DAY + "\": " + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_DAY)) + ",";
            json += "\"" + WeightEntry.COLUMN_RECORD_DATE + "\": \"" + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + WeightEntry.COLUMN_RECORD_TIME + "\": \"" + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_RECORD_TIME)) + "\",";
            json += "\"" + WeightEntry.COLUMN_FEED + "\": \"" + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_FEED)) + "\",";
            json += "\"" + WeightEntry.COLUMN_TIMESTAMP + "\": \"" + cursor.getString(cursor.getColumnIndex(WeightEntry.COLUMN_TIMESTAMP)) + "\",";
            json += WeightDetailController.getUploadJsonByWeightId(db, cursor.getLong(cursor.getColumnIndex(WeightEntry._ID)));
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
