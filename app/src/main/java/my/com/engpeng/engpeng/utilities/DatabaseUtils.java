package my.com.engpeng.engpeng.utilities;

import android.app.Person;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 18/1/2018.
 */

public class DatabaseUtils {

    public static void clearSystemData(SQLiteDatabase db) {
        db.delete(BranchEntry.TABLE_NAME, null, null);
        db.delete(HouseEntry.TABLE_NAME, null, null);
        db.delete(StandardWeightEntry.TABLE_NAME, null, null);
        db.delete(FeedItemEntry.TABLE_NAME, null, null);

        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + BranchEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + HouseEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + StandardWeightEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FeedItemEntry.TABLE_NAME + "'");
    }

    public static void clearTransactionData(SQLiteDatabase db) {
        db.delete(MortalityEntry.TABLE_NAME, null, null);
        db.delete(TempWeightEntry.TABLE_NAME, null, null);
        db.delete(TempWeightDetailEntry.TABLE_NAME, null, null);
        db.delete(WeightEntry.TABLE_NAME, null, null);
        db.delete(WeightDetailEntry.TABLE_NAME, null, null);

        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MortalityEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TempWeightEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TempWeightDetailEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + WeightEntry.TABLE_NAME + "'");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + WeightDetailEntry.TABLE_NAME + "'");
    }

    public static void insertBranch(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(BranchEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void insertHouse(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(HouseEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void insertStandardWeight(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(StandardWeightEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void insertFeedItem(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(FeedItemEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void insertPersonStaff(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(PersonStaffEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void insertLocation(SQLiteDatabase db, ContentValues[] cvs) {
        if (cvs != null) {
            for (ContentValues cv : cvs) {
                db.insert(LocationEntry.TABLE_NAME, null, cv);
            }
        }
    }

    public static void updateUploadedStatus(SQLiteDatabase db) {
        db.execSQL("UPDATE " + MortalityEntry.TABLE_NAME + " SET " + MortalityEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + CatchBTAEntry.TABLE_NAME + " SET " + CatchBTAEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + WeightEntry.TABLE_NAME + " SET " + WeightEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + FeedInEntry.TABLE_NAME + " SET " + FeedInEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + FeedTransferEntry.TABLE_NAME + " SET " + FeedInEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + FeedDischargeEntry.TABLE_NAME + " SET " + FeedDischargeEntry.COLUMN_UPLOAD + " = 1");
        db.execSQL("UPDATE " + FeedReceiveEntry.TABLE_NAME + " SET " + FeedReceiveEntry.COLUMN_UPLOAD + " = 1");
    }
}
