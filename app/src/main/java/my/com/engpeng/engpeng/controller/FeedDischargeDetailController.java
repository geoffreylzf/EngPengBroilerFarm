package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.FeedDischargeDetailEntry;

public class FeedDischargeDetailController {

    public static long add(SQLiteDatabase db,
                           long feed_discharge_id,
                           int house_code,
                           int item_packing_id,
                           double weight){

        ContentValues cv = new ContentValues();

        cv.put(FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID, feed_discharge_id);
        cv.put(FeedDischargeDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(FeedDischargeDetailEntry.COLUMN_WEIGHT, weight);

        return db.insert(FeedDischargeDetailEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByFeedDischargeId(SQLiteDatabase db, Long feed_discharge_id) {

        String selection = FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_discharge_id),
        };

        return db.query(
                FeedDischargeDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeDetailEntry._ID + " DESC"
        );
    }

    public static boolean removeByFeedDischargeId(SQLiteDatabase db, long feed_discharge_id) {
        return db.delete(FeedDischargeDetailEntry.TABLE_NAME, FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + "=" + feed_discharge_id, null) > 0;
    }

    public static String toQrData(SQLiteDatabase db, long feed_discharge_id){

        String[] columns = new String[]{
                FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID,
                "SUM(" + FeedDischargeDetailEntry.COLUMN_WEIGHT + ") AS " + FeedDischargeDetailEntry.COLUMN_WEIGHT,
        };

        String selection = FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_discharge_id),
        };

        String groupBy = FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID;

        Cursor cursor = db.query(
                FeedDischargeDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                null,
                FeedDischargeDetailEntry._ID + " DESC"
        );

        String data = "";

        while(cursor.moveToNext()){
            int item_packing_id = cursor.getInt(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID));
            String weight = cursor.getString(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_WEIGHT));

            data += "D";
            data += "|" + item_packing_id;
            data += "|" + weight;
            data += "\n";
        }

        return data;
    }

    public static Cursor getAllByFeedDischargeIdOrderByItemPackingId(SQLiteDatabase db, Long feed_discharge_id) {

        String selection = FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_discharge_id),
        };

        return db.query(
                FeedDischargeDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID
        );
    }

    public static double getTotalWeightByFeedDischargeIdItemPackingId(SQLiteDatabase db, Long feed_discharge_id, int item_packing_id) {

        String[] columns = new String[]{
                "SUM(" + FeedDischargeDetailEntry.COLUMN_WEIGHT + ") AS " + FeedDischargeDetailEntry.COLUMN_WEIGHT,
        };

        String selection = FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " = ? AND " +
                FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_discharge_id),
                String.valueOf(item_packing_id),
        };

        Cursor cursor =  db.query(
                FeedDischargeDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID
        );

        cursor.moveToFirst();

        double ttlWeight = cursor.getDouble(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_WEIGHT));

        return ttlWeight;
    }

    public static String getUploadJsonByFeedDischargeId(SQLiteDatabase db, Long feed_discharge_id) {
        String selection = FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_discharge_id),
        };

        Cursor cursor = db.query(
                FeedDischargeDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeDetailEntry._ID + " DESC"
        );

        String json = " \"" + FeedDischargeDetailEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedDischargeDetailEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID)) + ",";
            json += "\"" + FeedDischargeDetailEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(FeedDischargeDetailEntry.COLUMN_WEIGHT)) + "";
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
