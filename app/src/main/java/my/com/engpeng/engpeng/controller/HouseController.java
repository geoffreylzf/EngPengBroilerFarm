package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 4/1/2018.
 */

public class HouseController {
    public static Cursor getHouseCodeByLocationId(SQLiteDatabase db, int location_id) {

        String selection = HouseEntry.COLUMN_LOCATION_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(location_id),
        };

        return db.query(
                HouseEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                HouseEntry.COLUMN_HOUSE_CODE
        );
    }

    public static int getCountByLocationId(SQLiteDatabase db, int location_id) {

        String[] columns = new String[]{
                "COUNT(" + HouseEntry.COLUMN_HOUSE_CODE + ") AS COUNT",
        };

        String selection = HouseEntry.COLUMN_LOCATION_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(location_id),
        };

        Cursor cursor= db.query(
                HouseEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                HouseEntry.COLUMN_HOUSE_CODE
        );

        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("COUNT"));

        return count;
    }

    public static boolean checkExistByLocationIdHouseCode(SQLiteDatabase db, int location_id, int house_code) {

        String selection = HouseEntry.COLUMN_LOCATION_ID + " = ? AND " +
                HouseEntry.COLUMN_HOUSE_CODE + " = ? ";
        String[] selectionArgs = new String[]{
                String.valueOf(location_id),
                String.valueOf(house_code),
        };

        return db.query(
                HouseEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                HouseEntry.COLUMN_HOUSE_CODE
        ).getCount() > 0;
    }
}
