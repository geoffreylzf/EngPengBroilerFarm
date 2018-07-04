package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.FeedReceiveDetailEntry;

public class FeedReceiveDetailController {

    public static long add(SQLiteDatabase db,
                           long feed_receive_id,
                           int house_code,
                           int item_packing_id,
                           double weight) {

        ContentValues cv = new ContentValues();

        cv.put(FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID, feed_receive_id);
        cv.put(FeedReceiveDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(FeedReceiveDetailEntry.COLUMN_WEIGHT, weight);

        return db.insert(FeedReceiveDetailEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByFeedReceiveId(SQLiteDatabase db, Long feed_receive_id) {

        String selection = FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_receive_id),
        };

        return db.query(
                FeedReceiveDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveDetailEntry._ID + " DESC"
        );
    }

    public static boolean removeByFeedReceiveId(SQLiteDatabase db, long id) {
        return db.delete(FeedReceiveDetailEntry.TABLE_NAME, FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + "=" + id, null) > 0;
    }

    public static Cursor getAllByFeedReceiveIdOrderByItemPackingId(SQLiteDatabase db, Long feed_receive_id) {

        String selection = FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_receive_id),
        };

        return db.query(
                FeedReceiveDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID
        );
    }

    public static double getTotalWeightByFeedReceiveIdItemPackingId(SQLiteDatabase db, Long feed_receive_id, int item_packing_id) {

        String[] columns = new String[]{
                "SUM(" + FeedReceiveDetailEntry.COLUMN_WEIGHT + ") AS " + FeedReceiveDetailEntry.COLUMN_WEIGHT,
        };

        String selection = FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + " = ? AND " +
                FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_receive_id),
                String.valueOf(item_packing_id),
        };

        Cursor cursor =  db.query(
                FeedReceiveDetailEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID
        );

        cursor.moveToFirst();

        double ttlWeight = cursor.getDouble(cursor.getColumnIndex(FeedReceiveDetailEntry.COLUMN_WEIGHT));

        return ttlWeight;
    }

    public static String getUploadJsonByFeedReceiveId(SQLiteDatabase db, Long feed_receive_id) {
        String selection = FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_receive_id),
        };

        Cursor cursor = db.query(
                FeedReceiveDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveDetailEntry._ID + " DESC"
        );

        String json = " \"" + FeedReceiveDetailEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedReceiveDetailEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveDetailEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID)) + ",";
            json += "\"" + FeedReceiveDetailEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveDetailEntry.COLUMN_WEIGHT)) + "";
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
