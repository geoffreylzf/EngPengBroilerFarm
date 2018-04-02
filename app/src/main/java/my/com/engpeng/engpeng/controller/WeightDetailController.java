package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 15/1/2018.
 */

public class WeightDetailController {
    public static long add(SQLiteDatabase db,
                           long weight_id,
                           int section,
                           int weight,
                           int qty,
                           String gender) {
        ContentValues cv = new ContentValues();
        cv.put(WeightDetailEntry.COLUMN_WEIGHT_ID, weight_id);
        cv.put(WeightDetailEntry.COLUMN_SECTION, section);
        cv.put(WeightDetailEntry.COLUMN_WEIGHT, weight);
        cv.put(WeightDetailEntry.COLUMN_QTY, qty);
        cv.put(WeightDetailEntry.COLUMN_GENDER, gender);
        return db.insert(WeightDetailEntry.TABLE_NAME, null, cv);
    }

    public static int getTotalWgtByGenderWeightId(SQLiteDatabase db, Long weight_id, String gender) {

        String[] columns = new String[]{
                "SUM(" + WeightDetailEntry.COLUMN_WEIGHT + ") AS " + WeightDetailEntry.COLUMN_WEIGHT,
        };

        String selection = WeightDetailEntry.COLUMN_GENDER + " = ? AND "
                + WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(gender),
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        int totalWeight = cursor.getInt(cursor.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalQtyByGenderWeightId(SQLiteDatabase db, Long weight_id, String gender) {

        String[] columns = new String[]{
                "SUM(" + WeightDetailEntry.COLUMN_QTY + ") AS " + WeightDetailEntry.COLUMN_QTY,
        };

        String selection = WeightDetailEntry.COLUMN_GENDER + " = ? AND "
                + WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(gender),
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(WeightDetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static int getTotalWgtByWeightId(SQLiteDatabase db, Long weight_id) {

        String[] columns = new String[]{
                "SUM(" + WeightDetailEntry.COLUMN_WEIGHT + ") AS " + WeightDetailEntry.COLUMN_WEIGHT,
        };

        String selection = WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry.COLUMN_WEIGHT + " DESC"
        );

        cursor.moveToFirst();
        int totalWeight = cursor.getInt(cursor.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT));
        return totalWeight;
    }

    public static int getTotalQtyByWeightId(SQLiteDatabase db, Long weight_id) {

        String[] columns = new String[]{
                "SUM(" + WeightDetailEntry.COLUMN_QTY + ") AS " + WeightDetailEntry.COLUMN_QTY,
        };

        String selection = WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry.COLUMN_QTY + " DESC"
        );

        cursor.moveToFirst();
        int totalQty = cursor.getInt(cursor.getColumnIndex(WeightDetailEntry.COLUMN_QTY));
        return totalQty;
    }

    public static Cursor getAllByWeightId(SQLiteDatabase db, Long weight_id) {

        String selection = WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(weight_id),
        };

        return db.query(
                WeightDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry._ID + " DESC"
        );
    }

    public static int getMaxSectionByWeightId(SQLiteDatabase db, Long weight_id) {

        String[] columns = new String[]{
                "MAX(" + WeightDetailEntry.COLUMN_SECTION + ") AS " + WeightDetailEntry.COLUMN_SECTION,
        };

        String selection = WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry.COLUMN_SECTION + " DESC"
        );

        cursor.moveToFirst();
        int maxSection = cursor.getInt(cursor.getColumnIndex(WeightDetailEntry.COLUMN_SECTION));
        return maxSection;
    }

    public static boolean removeByWeightId(SQLiteDatabase db, long id) {
        return db.delete(WeightDetailEntry.TABLE_NAME, WeightDetailEntry.COLUMN_WEIGHT_ID + "=" + id, null) > 0;
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(WeightDetailEntry.TABLE_NAME, WeightDetailEntry._ID + "=" + id, null) > 0;
    }

    public static String getUploadJsonByWeightId(SQLiteDatabase db, Long weight_id) {
        String selection = WeightDetailEntry.COLUMN_WEIGHT_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(weight_id),
        };

        Cursor cursor = db.query(
                WeightDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WeightDetailEntry._ID + " DESC"
        );

        String json = " \"" + WeightDetailEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + WeightDetailEntry.COLUMN_SECTION + "\": " + cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_SECTION)) + ",";
            json += "\"" + WeightDetailEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT)) + ",";
            json += "\"" + WeightDetailEntry.COLUMN_QTY + "\": " + cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_QTY)) + ",";
            json += "\"" + WeightDetailEntry.COLUMN_GENDER + "\": \"" + cursor.getString(cursor.getColumnIndex(WeightDetailEntry.COLUMN_GENDER)) + "\"";
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
