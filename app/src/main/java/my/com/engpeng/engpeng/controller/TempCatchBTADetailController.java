package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 13/2/2018.
 */

public class TempCatchBTADetailController {

    public static long add(SQLiteDatabase db,
                           double weight,
                           int qty,
                           int house,
                           int cage_qty,
                           int with_cover_qty) {

        ContentValues cv = new ContentValues();
        cv.put(EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT, weight);
        cv.put(EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY, qty);
        cv.put(EngPengContract.TempCatchBTADetailEntry.COLUMN_HOUSE_CODE, house);
        cv.put(EngPengContract.TempCatchBTADetailEntry.COLUMN_CAGE_QTY, cage_qty);
        cv.put(EngPengContract.TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY, with_cover_qty);
        return db.insert(EngPengContract.TempCatchBTADetailEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAll(SQLiteDatabase db) {

        return db.query(
                EngPengContract.TempCatchBTADetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTADetailEntry._ID + " DESC"
        );
    }

    public static Cursor getLastRecord(SQLiteDatabase db) {

        Cursor cursor = db.query(
                EngPengContract.TempCatchBTADetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTADetailEntry._ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }


    }

    public static double getTotalWeight(SQLiteDatabase db) {

        String[] columns = new String[]{
                "SUM(" + EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT + ") AS " + EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT,
        };

        Cursor cursor = db.query(
                EngPengContract.TempCatchBTADetailEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        double totalWeight = cursor.getDouble(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalQty(SQLiteDatabase db) {

        String[] columns = new String[]{
                "SUM(" + EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY + ") AS " + EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY,
        };

        Cursor cursor = db.query(
                EngPengContract.TempCatchBTADetailEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(EngPengContract.TempCatchBTADetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(EngPengContract.TempCatchBTADetailEntry.TABLE_NAME, EngPengContract.TempCatchBTADetailEntry._ID + "=" + id, null) > 0;
    }


    public static void delete(SQLiteDatabase db) {
        db.delete(EngPengContract.TempCatchBTADetailEntry.TABLE_NAME, null, null);
    }
}
