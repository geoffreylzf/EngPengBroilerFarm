package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class FeedInDetailController {

    public static long add(SQLiteDatabase db,
                           long feed_in_id,
                           long doc_detail_id,
                           int house_code,
                           int item_packing_id,
                           String compartment_no,
                           double qty,
                           double weight){

        ContentValues cv = new ContentValues();

        cv.put(FeedInDetailEntry.COLUMN_FEED_IN_ID, feed_in_id);
        cv.put(FeedInDetailEntry.COLUMN_DOC_DETAIL_ID, doc_detail_id);
        cv.put(FeedInDetailEntry.COLUMN_HOUSE_CODE, house_code);
        cv.put(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(FeedInDetailEntry.COLUMN_COMPARTMENT_NO, compartment_no);
        cv.put(FeedInDetailEntry.COLUMN_QTY, qty);
        cv.put(FeedInDetailEntry.COLUMN_WEIGHT, weight);

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
            json += "\"" + FeedInDetailEntry.COLUMN_DOC_DETAIL_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_DOC_DETAIL_ID)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_HOUSE_CODE + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_HOUSE_CODE)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_ITEM_PACKING_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_COMPARTMENT_NO + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_COMPARTMENT_NO)) + "\",";
            json += "\"" + FeedInDetailEntry.COLUMN_QTY + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_QTY)) + ",";
            json += "\"" + FeedInDetailEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(FeedInDetailEntry.COLUMN_WEIGHT)) + "";
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
