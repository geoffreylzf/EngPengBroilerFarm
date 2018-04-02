package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 12/1/2018.
 */

public class TempWeightController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           int house_code,
                           int day,
                           String record_date,
                           String record_time,
                           String feed) {

        ContentValues cv = new ContentValues();
        cv.put(TempWeightEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(TempWeightEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(TempWeightEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(TempWeightEntry.COLUMN_DAY, day);
        cv.put(TempWeightEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(TempWeightEntry.COLUMN_RECORD_TIME, record_time);
        cv.put(TempWeightEntry.COLUMN_FEED, feed);
        return db.insert(TempWeightEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAll(SQLiteDatabase db) {
        return db.query(
                TempWeightEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TempWeightEntry._ID + " DESC"
        );
    }

    public static void delete(SQLiteDatabase db){
        db.delete(TempWeightEntry.TABLE_NAME, null, null);
    }
}
