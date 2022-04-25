package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 27/2/2018.
 */

public class CatchBTADetailController {

    public static long add(SQLiteDatabase db,
                           long catch_bta_id,
                           double wgt,
                           int qty,
                           int house_code,
                           int cage_qty,
                           int with_cover_qty,
                           int is_bt) {
        ContentValues cv = new ContentValues();

        cv.put(CatchBTADetailEntry.COLUMN_CATCH_BTA_ID, catch_bta_id);
        cv.put(CatchBTADetailEntry.COLUMN_WEIGHT, wgt);
        cv.put(CatchBTADetailEntry.COLUMN_QTY, qty);
        cv.put(CatchBTADetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(CatchBTADetailEntry.COLUMN_CAGE_QTY, cage_qty);
        cv.put(CatchBTADetailEntry.COLUMN_WITH_COVER_QTY, with_cover_qty);
        cv.put(CatchBTADetailEntry.COLUMN_IS_BT, is_bt);
        return db.insert(CatchBTADetailEntry.TABLE_NAME, null, cv);
    }

    public static double getTotalWeightByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String[] columns = new String[]{
                "SUM(" + CatchBTADetailEntry.COLUMN_WEIGHT + ") AS " + CatchBTADetailEntry.COLUMN_WEIGHT,
        };

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        double totalWeight = cursor.getDouble(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalQtyByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String[] columns = new String[]{
                "SUM(" + CatchBTADetailEntry.COLUMN_QTY + ") AS " + CatchBTADetailEntry.COLUMN_QTY,
        };

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static int getTotalCageByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String[] columns = new String[]{
                "SUM(" + CatchBTADetailEntry.COLUMN_CAGE_QTY + ") AS " + CatchBTADetailEntry.COLUMN_CAGE_QTY,
        };

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_CAGE_QTY));
    }

    public static int getTotalCoverByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String[] columns = new String[]{
                "SUM(" + CatchBTADetailEntry.COLUMN_WITH_COVER_QTY + ") AS " + CatchBTADetailEntry.COLUMN_WITH_COVER_QTY,
        };

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
    }

    public static Cursor getAllByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        return db.query(
                CatchBTADetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry._ID + " DESC"
        );
    }

    public static boolean removeByCatchBTAId(SQLiteDatabase db, long id) {
        return db.delete(CatchBTADetailEntry.TABLE_NAME, CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + "=" + id, null) > 0;
    }

    public static int[] getHouseCodeByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {

        String[] columns = new String[]{
                "DISTINCT(" + CatchBTADetailEntry.COLUMN_HOUSE_CODE + ") AS " + CatchBTADetailEntry.COLUMN_HOUSE_CODE,
        };

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry._ID + " ASC"
        );

        int[] houseArr = new int[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            houseArr[i] = cursor.getInt(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_HOUSE_CODE));
        }
        return houseArr;
    }

    public static Cursor getAllByCatchBTAIdHouseCode(SQLiteDatabase db, Long catch_bta_id, int house_code) {

        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? AND " +
                CatchBTADetailEntry.COLUMN_HOUSE_CODE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
                String.valueOf(house_code),
        };

        return db.query(
                CatchBTADetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry._ID
        );
    }

    public static String getUploadJsonByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {
        String selection = CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                CatchBTADetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTADetailEntry._ID + " DESC"
        );

        String json = " \"" + CatchBTADetailEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + CatchBTADetailEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_WEIGHT)) + ",";
            json += "\"" + CatchBTADetailEntry.COLUMN_QTY + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_QTY)) + ",";
            json += "\"" + CatchBTADetailEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + CatchBTADetailEntry.COLUMN_CAGE_QTY + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_CAGE_QTY)) + ",";
            json += "\"" + CatchBTADetailEntry.COLUMN_WITH_COVER_QTY + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_WITH_COVER_QTY)) + ",";
            json += "\"" + CatchBTADetailEntry.COLUMN_IS_BT + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTADetailEntry.COLUMN_IS_BT)) + "";
            json += "";
            if (cursor.getPosition() == (cursor.getCount() - 1)) {
                json += "}";
            } else {
                json += "},";
            }
        }
        json += "]";
        return json;
    }
}
