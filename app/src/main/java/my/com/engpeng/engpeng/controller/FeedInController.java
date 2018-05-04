package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class FeedInController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           int doc_number,
                           String type,
                           String truck_code){

        ContentValues cv = new ContentValues();
        cv.put(FeedInEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedInEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedInEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedInEntry.COLUMN_DOC_NUMBER, doc_number);
        cv.put(FeedInEntry.COLUMN_TYPE, type);
        cv.put(FeedInEntry.COLUMN_TRUCK_CODE, truck_code);

        return db.insert(FeedInEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                     int company_id,
                                     int location_id) {

        String selection = FeedInEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedInEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedInEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInEntry.COLUMN_RECORD_DATE + " DESC, " + FeedInEntry._ID + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedInEntry.TABLE_NAME, FeedInEntry._ID + "=" + id, null) > 0;
    }
}
