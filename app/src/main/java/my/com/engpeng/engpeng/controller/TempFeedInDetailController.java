package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class TempFeedInDetailController {

    public static Cursor getAll(SQLiteDatabase db) {
        return db.query(
                TempFeedInDetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TempFeedInDetailEntry._ID + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(TempFeedInDetailEntry.TABLE_NAME, TempFeedInDetailEntry._ID + "=" + id, null) > 0;
    }

    public static long add(SQLiteDatabase db,
                           int house_code,
                           int item_packing_id,
                           double qty) {

        ContentValues cv = new ContentValues();
        cv.put(TempFeedInDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(TempFeedInDetailEntry.COLUMN_QTY, qty);
        return db.insert(TempFeedInDetailEntry.TABLE_NAME, null, cv);
    }

    public static void delete(SQLiteDatabase db) {
        db.delete(TempFeedInDetailEntry.TABLE_NAME, null, null);
    }
}
