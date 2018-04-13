package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 12/1/2018.
 */

public class TempWeightDetailController {

    public static long add(SQLiteDatabase db,
                           int section,
                           int weight,
                           int qty,
                           String gender) {
        ContentValues cv = new ContentValues();
        cv.put(TempWeightDetailEntry.COLUMN_SECTION, section);
        cv.put(TempWeightDetailEntry.COLUMN_WEIGHT, weight);
        cv.put(TempWeightDetailEntry.COLUMN_QTY, qty);
        cv.put(TempWeightDetailEntry.COLUMN_GENDER, gender);
        return db.insert(TempWeightDetailEntry.TABLE_NAME, null, cv);
    }

    public static int getTotalWgtByGender(SQLiteDatabase db, String gender) {

        String[] columns = new String[]{
                "SUM(" + TempWeightDetailEntry.COLUMN_WEIGHT + ") AS " + TempWeightDetailEntry.COLUMN_WEIGHT,
        };

        String selection = TempWeightDetailEntry.COLUMN_GENDER + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(gender),
        };

        Cursor cursor = db.query(
                TempWeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                TempWeightDetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        int totalWeight = cursor.getInt(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalWgt(SQLiteDatabase db) {

        String[] columns = new String[]{
                "SUM(" + TempWeightDetailEntry.COLUMN_WEIGHT + ") AS " + TempWeightDetailEntry.COLUMN_WEIGHT,
        };

        Cursor cursor = db.query(
                TempWeightDetailEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                TempWeightDetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        int totalWeight = cursor.getInt(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalQtyByGender(SQLiteDatabase db, String gender) {

        String[] columns = new String[]{
                "SUM(" + TempWeightDetailEntry.COLUMN_QTY + ") AS " + TempWeightDetailEntry.COLUMN_QTY,
        };

        String selection = TempWeightDetailEntry.COLUMN_GENDER + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(gender),
        };

        Cursor cursor = db.query(
                TempWeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                TempWeightDetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static int getTotalQty(SQLiteDatabase db) {

        String[] columns = new String[]{
                "SUM(" + TempWeightDetailEntry.COLUMN_QTY + ") AS " + TempWeightDetailEntry.COLUMN_QTY,
        };

        Cursor cursor = db.query(
                TempWeightDetailEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                TempWeightDetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(TempWeightDetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static Cursor getAll(SQLiteDatabase db) {

        return db.query(
                TempWeightDetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                TempWeightDetailEntry._ID + " DESC"
        );
    }

    public static Cursor getLastRecord(SQLiteDatabase db) {

        Cursor cursor = db.query(
                EngPengContract.TempWeightDetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EngPengContract.TempWeightDetailEntry._ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(TempWeightDetailEntry.TABLE_NAME, TempWeightDetailEntry._ID + "=" + id, null) > 0;
    }

    public static void delete(SQLiteDatabase db) {
        db.delete(TempWeightDetailEntry.TABLE_NAME, null, null);
    }
}
