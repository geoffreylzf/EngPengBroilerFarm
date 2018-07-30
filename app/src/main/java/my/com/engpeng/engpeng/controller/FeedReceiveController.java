package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.Global.RUNNING_CODE_RECEIVE;
import static my.com.engpeng.engpeng.Global.sUsername;
import static my.com.engpeng.engpeng.data.EngPengContract.FeedReceiveEntry;

public class FeedReceiveController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String discharge_code,
                           String truck_code,
                           String running_no,
                           double variance) {

        ContentValues cv = new ContentValues();
        cv.put(FeedReceiveEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedReceiveEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedReceiveEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedReceiveEntry.COLUMN_DISCHARGE_CODE, discharge_code);
        cv.put(FeedReceiveEntry.COLUMN_RUNNING_NO, running_no);
        cv.put(FeedReceiveEntry.COLUMN_TRUCK_CODE, truck_code);
        cv.put(FeedReceiveEntry.COLUMN_VARIANCE, variance);

        return db.insert(FeedReceiveEntry.TABLE_NAME, null, cv);
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedReceiveEntry.TABLE_NAME, FeedReceiveEntry._ID + "=" + id, null) > 0;
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                    int company_id,
                                    int location_id) {

        String selection = FeedReceiveEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedReceiveEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveEntry.COLUMN_RECORD_DATE + " DESC, " + FeedReceiveEntry._ID + " DESC"
        );
    }

    public static Cursor getById(SQLiteDatabase db, Long id) {
        String selection = FeedReceiveEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };
        return db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = FeedReceiveEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = FeedReceiveEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedReceiveEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + FeedReceiveEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedReceiveEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + FeedReceiveEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + FeedReceiveEntry.COLUMN_RECORD_DATE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + FeedReceiveEntry.COLUMN_DISCHARGE_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_DISCHARGE_CODE)) + "\",";
            json += "\"" + FeedReceiveEntry.COLUMN_RUNNING_NO + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_RUNNING_NO)) + "\",";
            json += "\"" + FeedReceiveEntry.COLUMN_TRUCK_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_TRUCK_CODE)) + "\",";
            json += "\"" + FeedReceiveEntry.COLUMN_VARIANCE + "\": " + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_VARIANCE)) + ",";
            json += "\"" + FeedReceiveEntry.COLUMN_TIMESTAMP + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_TIMESTAMP)) + "\",";
            json += FeedReceiveDetailController.getUploadJsonByFeedReceiveId(db, cursor.getLong(cursor.getColumnIndex(FeedReceiveEntry._ID)));
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
        String selection = FeedReceiveEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                FeedReceiveEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            long feed_receive_id = cursor.getLong(cursor.getColumnIndex(FeedReceiveEntry._ID));
            remove(db, feed_receive_id);
            FeedReceiveDetailController.removeByFeedReceiveId(db, feed_receive_id);
        }
    }

    public static String getLastRunningNo(SQLiteDatabase db) {
        String running_no = "";
        String filter = RUNNING_CODE_RECEIVE + "-" + sUsername + "-";

        String[] columns = new String[]{
                "MAX(" + FeedReceiveEntry.COLUMN_RUNNING_NO + ") AS " + FeedReceiveEntry.COLUMN_RUNNING_NO,
        };

        String selection = FeedReceiveEntry.COLUMN_RUNNING_NO + " LIKE '" + filter + "%' ";

        Cursor cursor = db.query(
                FeedReceiveEntry.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            running_no = cursor.getString(cursor.getColumnIndex(FeedReceiveEntry.COLUMN_RUNNING_NO));
            if (running_no == null) {
                running_no = "";
            }
        }
        return running_no;
    }
}
