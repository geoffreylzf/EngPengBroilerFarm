package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

public class LocationController {

    public static Cursor getAllByFilter(SQLiteDatabase db, String filter) {
        String selection = EngPengContract.LocationEntry.COLUMN_LOCATION_NAME + " LIKE '%" + filter + "%' ";
        return db.query(
                EngPengContract.LocationEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                EngPengContract.LocationEntry.COLUMN_LOCATION_NAME
        );
    }

    public static int deleteAll(SQLiteDatabase db) {
        return db.delete(EngPengContract.LocationEntry.TABLE_NAME,
                null,
                null);
    }
}
