package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static my.com.engpeng.engpeng.data.EngPengContract.CatchBTAEntry;

/**
 * Created by Admin on 27/2/2018.
 */

public class CatchBTAController {

    public static long add(SQLiteDatabase db,
                           int company_id,
                           int location_id,
                           String record_date,
                           String type,
                           int doc_number,
                           String doc_type,
                           String truck_code,
                           String code,
                           String fasting_time) {

        ContentValues cv = new ContentValues();
        cv.put(CatchBTAEntry.COLUMN_COMPANY_ID, company_id);
        cv.put(CatchBTAEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(CatchBTAEntry.COLUMN_RECORD_DATE, record_date);
        cv.put(CatchBTAEntry.COLUMN_TYPE, type);
        cv.put(CatchBTAEntry.COLUMN_DOC_NUMBER, doc_number);
        cv.put(CatchBTAEntry.COLUMN_DOC_TYPE, doc_type);
        cv.put(CatchBTAEntry.COLUMN_TRUCK_CODE, truck_code);
        cv.put(CatchBTAEntry.COLUMN_CODE, code);
        cv.put(CatchBTAEntry.COLUMN_FASTING_TIME, fasting_time);

        return db.insert(CatchBTAEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCLU(SQLiteDatabase db,
                                     int company_id,
                                     int location_id) {

        String selection = CatchBTAEntry.COLUMN_COMPANY_ID + " = ? AND " +
                CatchBTAEntry.COLUMN_LOCATION_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
                String.valueOf(location_id),
        };

        return db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry.COLUMN_RECORD_DATE + " DESC, " + CatchBTAEntry._ID + " DESC"
        );
    }

    public static Cursor getById(SQLiteDatabase db, Long id) {
        String selection = CatchBTAEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        return db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry.COLUMN_RECORD_DATE + " DESC"
        );
    }

    public static boolean checkDocNumber(SQLiteDatabase db, int doc_number, String doc_type) {
        String selection = CatchBTAEntry.COLUMN_DOC_NUMBER + " = ? AND " +
                CatchBTAEntry.COLUMN_DOC_TYPE + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(doc_number),
                doc_type
        };

        return db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount() == 0;
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(CatchBTAEntry.TABLE_NAME, CatchBTAEntry._ID + "=" + id, null) > 0;
    }

    public static void removeUploaded(SQLiteDatabase db) {
        String selection = CatchBTAEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(1),
        };

        Cursor cursor = db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            long catch_bta_id = cursor.getLong(cursor.getColumnIndex(CatchBTAEntry._ID));
            remove(db, catch_bta_id);
            CatchBTADetailController.removeByCatchBTAId(db, catch_bta_id);
        }
    }

    public static int reupload(SQLiteDatabase db, Long id) {

        ContentValues cv = new ContentValues();
        cv.put(CatchBTAEntry.COLUMN_UPLOAD, 0);

        String selection = CatchBTAEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        return db.update(
                CatchBTAEntry.TABLE_NAME,
                cv,
                selection,
                selectionArgs
        );

    }

    public static int getCount(SQLiteDatabase db, int upload) {
        String selection = CatchBTAEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        return db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry.COLUMN_RECORD_DATE + " DESC"
        ).getCount();
    }

    public static int increasePrintCount(SQLiteDatabase db, long id) {
        Cursor cursor = getById(db, id);
        cursor.moveToFirst();
        int print_count = cursor.getInt(cursor.getColumnIndex(CatchBTAEntry.COLUMN_PRINT_COUNT));
        print_count++;

        ContentValues cv = new ContentValues();
        cv.put(CatchBTAEntry.COLUMN_PRINT_COUNT, print_count);
        return db.update(CatchBTAEntry.TABLE_NAME, cv, CatchBTAEntry._ID + "=" + id, null);
    }

    public static String getUploadJson(SQLiteDatabase db, int upload) {
        String selection = CatchBTAEntry.COLUMN_UPLOAD + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(upload),
        };

        Cursor cursor = db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry.COLUMN_RECORD_DATE + " DESC"
        );

        String json = "{ \"" + CatchBTAEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";
            json += "\"" + CatchBTAEntry.COLUMN_COMPANY_ID + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_COMPANY_ID)) + ",";
            json += "\"" + CatchBTAEntry.COLUMN_LOCATION_ID + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_LOCATION_ID)) + ",";
            json += "\"" + CatchBTAEntry.COLUMN_RECORD_DATE + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_RECORD_DATE)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_TYPE + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TYPE)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_DOC_NUMBER + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_NUMBER)) + ",";
            json += "\"" + CatchBTAEntry.COLUMN_DOC_TYPE + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_TYPE)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_TRUCK_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TRUCK_CODE)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_CODE + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_CODE)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_FASTING_TIME + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_FASTING_TIME)) + "\",";
            json += "\"" + CatchBTAEntry.COLUMN_PRINT_COUNT + "\": " + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_PRINT_COUNT)) + ",";
            json += "\"" + CatchBTAEntry.COLUMN_TIMESTAMP + "\": \"" + cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TIMESTAMP)) + "\",";
            json += CatchBTADetailController.getUploadJsonByCatchBTAId(db, cursor.getLong(cursor.getColumnIndex(CatchBTAEntry._ID)));
            if (cursor.getPosition() == (cursor.getCount() - 1)) {
                json += "}";
            } else {
                json += "},";
            }
        }
        json += "]}";
        return json;
    }

    public static String toQrData(SQLiteDatabase db, long id) {

        String selection = CatchBTAEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };

        Cursor cursor = db.query(
                CatchBTAEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                CatchBTAEntry._ID + " DESC"
        );

        String data = "";

        if (cursor.moveToFirst()) {
            String companyId = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_COMPANY_ID));
            String locationId = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_LOCATION_ID));
            String recordDate = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_RECORD_DATE));
            String docNumber = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_NUMBER));
            String docType = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_DOC_TYPE));
            String type = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TYPE));
            String truckCode = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_TRUCK_CODE));
            String code = cursor.getString(cursor.getColumnIndex(CatchBTAEntry.COLUMN_CODE));

            data = "v1";
            data += "|" + companyId;
            data += "|" + locationId;
            data += "|" + recordDate;
            data += "|" + docNumber;
            data += "|" + docType;
            data += "|" + type;
            data += "|" + truckCode;
            data += "|" + code;

            int[] houseArr = CatchBTADetailController.getHouseCodeByCatchBTAId(db, id);
            String houseStr = "";
            for (int i = 0; i < houseArr.length; i++) {
                houseStr += String.valueOf(houseArr[i]);
                if (i != (houseArr.length - 1)) {
                    houseStr += ",";
                }
            }
            data += "|" + houseStr;

            int ttlQty = CatchBTADetailController.getTotalQtyByCatchBTAId(db, id);
            data += "|" + String.valueOf(ttlQty);
        }
        return data;
    }
}
