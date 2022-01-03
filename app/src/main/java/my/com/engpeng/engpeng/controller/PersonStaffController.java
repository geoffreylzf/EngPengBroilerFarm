package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

public class PersonStaffController {
    public static Cursor getAllByFilter(SQLiteDatabase db, String filter) {
        String selection = EngPengContract.PersonStaffEntry.COLUMN_PERSON_NAME + " LIKE '%" + filter + "%' ";
        return db.query(
                EngPengContract.PersonStaffEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                EngPengContract.PersonStaffEntry.COLUMN_PERSON_NAME
        );
    }

    public static int deleteAll(SQLiteDatabase db) {
        return db.delete(EngPengContract.PersonStaffEntry.TABLE_NAME,
                null,
                null);
    }
}
