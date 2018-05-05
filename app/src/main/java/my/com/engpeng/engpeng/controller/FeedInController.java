package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class FeedInController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           int doc_number,
                           String type,
                           String truck_code){

        ContentValues cv = new ContentValues();
        cv.put(FeedInEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedInEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedInEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedInEntry.COLUMN_DOC_NUMBER, doc_number);
        cv.put(FeedInEntry.COLUMN_TYPE, type);
        cv.put(FeedInEntry.COLUMN_TRUCK_CODE, truck_code);

        return db.insert(FeedInEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                     int company_id,
                                     int location_id) {

        String selection = FeedInEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedInEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedInEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInEntry.COLUMN_RECORD_DATE + " DESC, " + FeedInEntry._ID + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedInEntry.TABLE_NAME, FeedInEntry._ID + "=" + id, null) > 0;
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = FeedInEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                FeedInEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = FeedInEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                FeedInEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedInEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + FeedInEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedInEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + FeedInEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + FeedInEntry.COLUMN_RECORD_DATE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + FeedInEntry.COLUMN_TYPE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_TYPE)) + "\",";
            json += "\"" + FeedInEntry.COLUMN_DOC_NUMBER + "\": " + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_DOC_NUMBER)) + ",";
            json += "\"" + FeedInEntry.COLUMN_TRUCK_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_TRUCK_CODE)) + "\",";
            json += "\"" + FeedInEntry.COLUMN_TIMESTAMP + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedInEntry.COLUMN_TIMESTAMP)) + "\",";
            json += FeedInDetailController.getUploadJsonByFeedInId(db, cursor.getLong(cursor.getColumnIndex(FeedInEntry._ID)));
            if (cursor.getPosition() == (cursor.getCount() - 1)) {
                json += "}";
            } else {
                json += "},";
            }
        }
        json += "]}";
        return json;
    }

    public static void removeUploaded(SQLiteDatabase db) {
        String selection = FeedInEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                FeedInEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while(cursor.moveToNext()){
            long feed_in_id = cursor.getLong(cursor.getColumnIndex(FeedInEntry._ID));
            remove(db, feed_in_id);
            FeedInDetailController.removeByFeedInId(db, feed_in_id);
        }
    }
}
