package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import static my.com.engpeng.engpeng.data.EngPengContract.TempFeedReceiveDetailEntry;

public class TempFeedReceiveDetailController {

    public static Cursor getAll(SQLiteDatabase db) {
        return db.query(
                TempFeedReceiveDetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TempFeedReceiveDetailEntry._ID + " DESC"
        );
    }

    public static void delete(SQLiteDatabase db) {
        db.delete(TempFeedReceiveDetailEntry.TABLE_NAME, null, null);
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(TempFeedReceiveDetailEntry.TABLE_NAME, TempFeedReceiveDetailEntry._ID + "=" + id, null) > 0;
    }

    public static long add(SQLiteDatabase db,
                           int house_code,
                           int item_packing_id,
                           double weight) {

        ContentValues cv = new ContentValues();
        cv.put(TempFeedReceiveDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(TempFeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(TempFeedReceiveDetailEntry.COLUMN_WEIGHT, weight);
        return db.insert(TempFeedReceiveDetailEntry.TABLE_NAME, null, cv);
    }
}
