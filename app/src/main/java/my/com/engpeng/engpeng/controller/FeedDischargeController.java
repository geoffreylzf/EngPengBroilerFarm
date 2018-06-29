package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static my.com.engpeng.engpeng.data.EngPengContract.FeedDischargeEntry;

public class FeedDischargeController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String discharge_code,
                           String truck_code) {

        ContentValues cv = new ContentValues();
        cv.put(FeedDischargeEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(FeedDischargeEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(FeedDischargeEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(FeedDischargeEntry.COLUMN_DISCHARGE_CODE, discharge_code);
        cv.put(FeedDischargeEntry.COLUMN_TRUCK_CODE, truck_code);

        return db.insert(FeedDischargeEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCL(SQLiteDatabase db,
                                    int company_id,
                                    int location_id) {

        String selection = FeedDischargeEntry.COLUMN_COMPANY_ID + " = ? AND " +
                FeedDischargeEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeEntry.COLUMN_RECORD_DATE + " DESC, " + FeedDischargeEntry._ID + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(FeedDischargeEntry.TABLE_NAME, FeedDischargeEntry._ID + "=" + id, null) > 0;
    }

    public static String toQrData(SQLiteDatabase db, long id) {
        String selection = FeedDischargeEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        Cursor cursor = db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeEntry._ID + " DESC"
        );

        String data = "";
        if (cursor.moveToFirst()) {
            String discharge_code = cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_DISCHARGE_CODE));
            String truck_code = cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_TRUCK_CODE));

            data += "H";
            data += "|FEED_DISCHARGE";
            data += "|" + discharge_code;
            data += "|" + truck_code;
            data += "\n";

            data += FeedDischargeDetailController.toQrData(db, id);
        }

        return data;
    }

    public static Cursor getById(SQLiteDatabase db, Long id) {
        String selection = FeedDischargeEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };
        return db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = FeedDischargeEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = FeedDischargeEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                FeedDischargeEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + FeedDischargeEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + FeedDischargeEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + FeedDischargeEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + FeedDischargeEntry.COLUMN_RECORD_DATE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + FeedDischargeEntry.COLUMN_DISCHARGE_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_DISCHARGE_CODE)) + "\",";
            json += "\"" + FeedDischargeEntry.COLUMN_TRUCK_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_TRUCK_CODE)) + "\",";
            json += "\"" + FeedDischargeEntry.COLUMN_TIMESTAMP + "\": \"" + cursor.getString(cursor.getColumnIndex(FeedDischargeEntry.COLUMN_TIMESTAMP)) + "\",";
            json += FeedDischargeDetailController.getUploadJsonByFeedDischargeId(db, cursor.getLong(cursor.getColumnIndex(FeedDischargeEntry._ID)));
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
        String selection = FeedDischargeEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                FeedDischargeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            long feed_discharge_id = cursor.getLong(cursor.getColumnIndex(FeedDischargeEntry._ID));
            remove(db, feed_discharge_id);
            FeedDischargeDetailController.removeByFeedDischargeId(db, feed_discharge_id);
        }
    }
}
