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
                           Long doc_detail_id,
                           int house_code,
                           int item_packing_id,
                           String compartment_no,
                           double qty,
                           double weight) {

        ContentValues cv = new ContentValues();
        cv.put(TempFeedInDetailEntry.COLUMN_DOC_DETAIL_ID, doc_detail_id);
        cv.put(TempFeedInDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(TempFeedInDetailEntry.COLUMN_COMPARTMENT_NO, compartment_no);
        cv.put(TempFeedInDetailEntry.COLUMN_QTY, qty);
        cv.put(TempFeedInDetailEntry.COLUMN_WEIGHT, weight);
        return db.insert(TempFeedInDetailEntry.TABLE_NAME, null, cv);
    }

    public static void delete(SQLiteDatabase db) {
        db.delete(TempFeedInDetailEntry.TABLE_NAME, null, null);
    }

    public static Cursor getAllByByItemPackingId(SQLiteDatabase db, int item_packing_id){
        String selection = TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(item_packing_id),
        };

        return db.query(
                TempFeedInDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public static Cursor getAllByByItemPackingIdCompartment(SQLiteDatabase db, int item_packing_id, String compartment_no){

        String selection = TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID + " = ? AND " +
                TempFeedInDetailEntry.COLUMN_COMPARTMENT_NO + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(item_packing_id),
                String.valueOf(compartment_no),
        };

        return db.query(
                TempFeedInDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
}
