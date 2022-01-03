package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

public class CatchBTAWorkerController {

    public static long add(SQLiteDatabase db,
                           long catch_bta_id,
                           Integer person_staff_id,
                           String worker_name) {
        ContentValues cv = new ContentValues();

        cv.put(EngPengContract.CatchBTAWorkerEntry.COLUMN_CATCH_BTA_ID, catch_bta_id);
        cv.put(EngPengContract.CatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID, person_staff_id);
        cv.put(EngPengContract.CatchBTAWorkerEntry.COLUMN_WORKER_NAME, worker_name);
        return db.insert(EngPengContract.CatchBTAWorkerEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAllByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {
        String selection = EngPengContract.CatchBTAWorkerEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        return db.query(
                EngPengContract.CatchBTAWorkerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.CatchBTAWorkerEntry._ID + " DESC"
        );
    }

    public static String getUploadJsonByCatchBTAId(SQLiteDatabase db, Long catch_bta_id) {
        String selection = EngPengContract.CatchBTAWorkerEntry.COLUMN_CATCH_BTA_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(catch_bta_id),
        };

        Cursor cursor = db.query(
                EngPengContract.CatchBTAWorkerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.CatchBTAWorkerEntry._ID + " DESC"
        );

        String json = " \"" + EngPengContract.CatchBTAWorkerEntry.TABLE_NAME + "\": [";
        while (cursor.moveToNext()) {
            json += "{";

            String psIdStr = cursor.getString(cursor.getColumnIndex(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID));
            Integer psId = null;
            try {
                psId = Integer.valueOf(psIdStr);
            } catch (NumberFormatException ex) {
                //DO Nothing
            }
            json += "\"" + EngPengContract.CatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID + "\": " + psId + ",";
            json += "\"" + EngPengContract.CatchBTAWorkerEntry.COLUMN_WORKER_NAME + "\": \"" + cursor.getString(cursor.getColumnIndex(EngPengContract.CatchBTAWorkerEntry.COLUMN_WORKER_NAME)) + "\"";
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
