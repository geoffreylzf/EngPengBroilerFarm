package my.com.engpeng.engpeng.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

public class StandardWeightController {

    public static String TYPE_OVERALL = "As-Hatched";
    public static String TYPE_MALE = "Male";
    public static String TYPE_FEMALE = "Female";

    public static int getAvgWeightByTypeDay(SQLiteDatabase db, String type, int day) {

        int avg_weight = 0;

        String selection = StandardWeightEntry.COLUMN_TYPE + " = ? AND "
                + StandardWeightEntry.COLUMN_DAY + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(type),
                String.valueOf(day),
        };

        Cursor cursor= db.query(
                StandardWeightEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            avg_weight = cursor.getInt(cursor.getColumnIndex(StandardWeightEntry.COLUMN_AVG_WEIGHT));
        }

        return avg_weight;
    }
}
