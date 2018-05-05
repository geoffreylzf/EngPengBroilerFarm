package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class FeedInDetailController {

    public static long add(SQLiteDatabase db,
                           long feed_in_id,
                           int house_code,
                           int item_packing_id,
                           double qty){

        ContentValues cv = new ContentValues();

        cv.put(FeedInDetailEntry.COLUMN_FEED_IN_ID, feed_in_id);
        cv.put(FeedInDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(FeedInDetailEntry.COLUMN_QTY, qty);

        return db.insert(FeedInDetailEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByFeedInId(SQLiteDatabase db, Long feed_in_id) {

        String selection = FeedInDetailEntry.COLUMN_FEED_IN_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_in_id),
        };

        return db.query(
                FeedInDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInDetailEntry._ID + " DESC"
        );
    }

    public static boolean removeByFeedInId(SQLiteDatabase db, long id) {
        return db.delete(FeedInDetailEntry.TABLE_NAME, FeedInDetailEntry.COLUMN_FEED_IN_ID + "=" + id, null) > 0;
    }

    public static String getUploadJsonByFeedInId(SQLiteDatabase db, Long feed_in) {
        String selection = FeedInDetailEntry.COLUMN_FEED_IN_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(feed_in),
        };

        Cursor cursor = db.query(
                FeedInDetailEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInDetailEntry._ID + " DESC"
        );

        String json = " \"" + FeedInDetailEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedInDetailEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_ITEM_PACKING_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_QTY + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_QTY)) + "";
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
