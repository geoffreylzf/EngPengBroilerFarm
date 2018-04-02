package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

/**
 * Created by Admin on 5/1/2018.
 */

public class BranchController {
    public static Cursor getBranchByErpId(SQLiteDatabase db, int erp_id) {

        String selection = EngPengContract.BranchEntry.COLUMN_ERP_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(erp_id),
        };

        return db.query(
                EngPengContract.BranchEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.BranchEntry._ID
        );
    }

    public static Cursor getAllCompany(SQLiteDatabase db) {

        String selection = EngPengContract.BranchEntry.COLUMN_COMPANY_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(0),
        };

        return db.query(
                EngPengContract.BranchEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.BranchEntry._ID
        );
    }

    public static Cursor getLocationByCompanyId(SQLiteDatabase db, int company_id) {

        String selection = EngPengContract.BranchEntry.COLUMN_COMPANY_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(company_id),
        };

        return db.query(
                EngPengContract.BranchEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.BranchEntry._ID
        );
    }
}
