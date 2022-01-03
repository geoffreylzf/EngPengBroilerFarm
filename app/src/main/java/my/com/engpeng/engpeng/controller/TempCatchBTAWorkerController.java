package my.com.engpeng.engpeng.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

public class TempCatchBTAWorkerController {

    public static long add(SQLiteDatabase db,
                           Integer personStaffId,
                           String workerName) {

        ContentValues cv = new ContentValues();
        cv.put(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID, personStaffId);
        cv.put(EngPengContract.TempCatchBTAWorkerEntry.COLUMN_WORKER_NAME, workerName);
        return db.insert(EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME, null, cv);
    }

    public static Cursor getAll(SQLiteDatabase db) {
        return db.query(
                EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EngPengContract.TempCatchBTAWorkerEntry._ID + " DESC"
        );
    }

    public static boolean remove(SQLiteDatabase db, long id) {
        return db.delete(EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME, EngPengContract.TempCatchBTAWorkerEntry._ID + "=" + id, null) > 0;
    }

    public static void deleteAll(SQLiteDatabase db) {
        db.delete(EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME, null, null);
    }

    public static Cursor getById(SQLiteDatabase db, int id) {
        String selection = EngPengContract.TempCatchBTAWorkerEntry._ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(id),
        };
        return db.query(
                EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public static boolean removeByPersonStaffId(SQLiteDatabase db, int psId) {
        return db.delete(EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME,
                EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID + "=" + psId,
                null) > 0;
    }

    public static Cursor getByPersonStaffId(SQLiteDatabase db, int psId) {
        String selection = EngPengContract.TempCatchBTAWorkerEntry.COLUMN_PERSON_STAFF_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(psId),
        };
        return db.query(
                EngPengContract.TempCatchBTAWorkerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
}
