package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 13/2/2018.
 */

public class TempCatchBTAController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String type,
                           int doc_number,
                           String doc_type,
                           String truck_code) {

        ContentValues cv = new ContentValues();
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_TYPE, type);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_DOC_NUMBER, doc_number);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_DOC_TYPE, doc_type);
        cv.put(EngPengContract.TempCatchBTAEntry.COLUMN_TRUCK_CODE, truck_code);
        return db.insert(EngPengContract.TempCatchBTAEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAll(SQLiteDatabase db) {
        return db.query(
                EngPengContract.TempCatchBTAEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTAEntry._ID + " DESC"
        );
    }

    public static void delete(SQLiteDatabase db) {
        db.delete(EngPengContract.TempCatchBTAEntry.TABLE_NAME, null, null);
    }
}
