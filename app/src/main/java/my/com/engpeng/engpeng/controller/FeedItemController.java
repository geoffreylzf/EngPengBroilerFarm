package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

public class FeedItemController {

    public static int BG_UOM_ID = 9;
    public static int MT_UOM_ID = 12;

    public static Cursor getByUom(SQLiteDatabase db, int type) {

        String selection = EngPengContract.FeedItemEntry.COLUMN_ITEM_UOM_ID + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(type),
        };

        return db.query(
                EngPengContract.FeedItemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.FeedItemEntry._ID
        );
    }

    public static Cursor getByMultiErpId(SQLiteDatabase db, String str_erp_id) {
        String selection = EngPengContract.FeedItemEntry.COLUMN_ERP_ID + " IN (" + str_erp_id + ") ";

        return db.query(
                EngPengContract.FeedItemEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                EngPengContract.FeedItemEntry._ID
        );
    }

    public static Cursor getByErpId(SQLiteDatabase db, int erp_id) {
        String selection = EngPengContract.FeedItemEntry.COLUMN_ERP_ID  + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(erp_id),
        };

        return db.query(
                EngPengContract.FeedItemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                EngPengContract.FeedItemEntry._ID
        );
    }
}
