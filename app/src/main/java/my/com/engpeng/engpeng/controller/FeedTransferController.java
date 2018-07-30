package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.Global.RUNNING_CODE_TRANSFER;
import static my.com.engpeng.engpeng.Global.sUsername;
import static my.com.engpeng.engpeng.data.EngPengContract.FeedTransferEntry;

public class FeedTransferController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String running_no,
                           int discharge_house,
                           int receive_house,
                           int item_packing_id,
                           double weight) {

        ContentValues cv = new ContentValues();
        cv.put(FeedTransferEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedTransferEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedTransferEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedTransferEntry.COLUMN_RUNNING_NO, running_no);
        cv.put(FeedTransferEntry.COLUMN_DISCHARGE_HOUSE, discharge_house);
        cv.put(FeedTransferEntry.COLUMN_RECEIVE_HOUSE, receive_house);
        cv.put(FeedTransferEntry.COLUMN_ITEM_PACKING_ID, item_packing_id);
        cv.put(FeedTransferEntry.COLUMN_WEIGHT, weight);
        return db.insert(FeedTransferEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                    int company_id,
                                    int location_id) {

        String selection = FeedTransferEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedTransferEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedTransferEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedTransferEntry.COLUMN_RECORD_DATE + " DESC, " + FeedTransferEntry._ID + " DESC"
        );
    }

    public static Cursor getById(SQLiteDatabase db, long id) {

        String selection = FeedTransferEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        return db.query(
                FeedTransferEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedTransferEntry.COLUMN_RECORD_DATE + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedTransferEntry.TABLE_NAME, FeedTransferEntry._ID + "=" + id, null) > 0;
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = FeedTransferEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                FeedTransferEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedTransferEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = FeedTransferEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                FeedTransferEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedTransferEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + FeedTransferEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedTransferEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_RECORD_DATE + "\":\" " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + FeedTransferEntry.COLUMN_RUNNING_NO + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RUNNING_NO)) + "\",";
            json += "\"" + FeedTransferEntry.COLUMN_DISCHARGE_HOUSE + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_DISCHARGE_HOUSE)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_RECEIVE_HOUSE + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RECEIVE_HOUSE)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_ITEM_PACKING_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_ITEM_PACKING_ID)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_WEIGHT + "\": " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_WEIGHT)) + ",";
            json += "\"" + FeedTransferEntry.COLUMN_TIMESTAMP + "\":\" " + cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_TIMESTAMP)) + "\"";
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
        String selection = FeedTransferEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                FeedTransferEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            long feed_transfer_id = cursor.getLong(cursor.getColumnIndex(FeedTransferEntry._ID));
            remove(db, feed_transfer_id);
        }
    }

    public static String getLastRunningNo(SQLiteDatabase db) {
        String running_no = "";
        String filter = RUNNING_CODE_TRANSFER + "-" + sUsername + "-";

        String[] columns = new String[]{
                "MAX(" + FeedTransferEntry.COLUMN_RUNNING_NO + ") AS " + FeedTransferEntry.COLUMN_RUNNING_NO,
        };

        String selection = FeedTransferEntry.COLUMN_RUNNING_NO + " LIKE '" + filter + "%' ";

        Cursor cursor = db.query(
                FeedTransferEntry.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            running_no = cursor.getString(cursor.getColumnIndex(FeedTransferEntry.COLUMN_RUNNING_NO));
            if (running_no == null) {
                running_no = "";
            }
        }
        return running_no;
    }
}
