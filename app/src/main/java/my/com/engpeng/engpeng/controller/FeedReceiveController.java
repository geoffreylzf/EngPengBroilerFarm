package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.FeedReceiveEntry;

public class FeedReceiveController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String discharge_code,
                           String truck_code) {

        ContentValues cv = new ContentValues();
        cv.put(FeedReceiveEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedReceiveEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedReceiveEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedReceiveEntry.COLUMN_DISCHARGE_CODE, discharge_code);
        cv.put(FeedReceiveEntry.COLUMN_TRUCK_CODE, truck_code);

        return db.insert(FeedReceiveEntry.TABLE_NAME, null, cv);
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedReceiveEntry.TABLE_NAME, FeedReceiveEntry._ID + "=" + id, null) > 0;
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                    int company_id,
                                    int location_id) {

        String selection = FeedReceiveEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedReceiveEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveEntry.COLUMN_RECORD_DATE + " DESC, " + FeedReceiveEntry._ID + " DESC"
        );
    }

    public static Cursor getById(SQLiteDatabase db, Long id) {
        String selection = FeedReceiveEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };
        return db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
}
