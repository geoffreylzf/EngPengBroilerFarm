package my.com.engpeng.engpeng.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 4/1/2018.
 */

public class EngPengDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "engpeng.db";
    private static final int DATABASE_VERSION = 15;
    //DB VER 7 20180317
    //DB VER 8 20180320
    //DB VER 9 20180418
    //DB VER 10 20180430
    //DB VER 11 20180611
    //DB VER 12 20180620
    //DB VER 13 20180622
    //DB VER 14 20190305
    //DB VER 15 20190327

    public EngPengDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_BRANCH_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BRANCH_HOUSE_SETUP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MORTALITY_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_CATCH_BTA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATCH_BTA_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_CATCH_BTA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_CATCH_BTA_DETAIL_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_WEIGHT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_WEIGHT_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEIGHT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEIGHT_DETAIL_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_STANDARD_WEIGHT_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_FEED_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_IN_DETAIL_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_FEED_TRANSFER_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_FEED_DISCHARGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_DISCHARGE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_DISCHARGE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_RECEIVE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEED_RECEIVE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_RECEIVE_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {

        if (oldVer <= 5) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS catch");

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS catch_bta");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS catch_bta_detail");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_catch_bta");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_catch_bta_detail");

            sqLiteDatabase.execSQL(SQL_CREATE_CATCH_BTA_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_CATCH_BTA_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_CATCH_BTA_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_CATCH_BTA_DETAIL_TABLE);

        }

        if (oldVer <= 6) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS weight");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS weight_detail");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_weight");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_weight_detail");

            sqLiteDatabase.execSQL(SQL_CREATE_WEIGHT_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_WEIGHT_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_WEIGHT_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_WEIGHT_DETAIL_TABLE);

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_catch_bta");
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_CATCH_BTA_TABLE);

            sqLiteDatabase.execSQL("BEGIN TRANSACTION;");
            sqLiteDatabase.execSQL("CREATE TABLE catch_bta_backup (_id INTEGER PRIMARY KEY AUTOINCREMENT,company_id INTEGER,location_id INTEGER, record_date DATE, type TEXT, doc_number INTEGER, doc_type TEXT, truck_code TEXT, upload INTEGER DEFAULT 0, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP );");
            sqLiteDatabase.execSQL("INSERT INTO catch_bta_backup SELECT _id, company_id, location_id, record_date, type, doc_number, doc_type, truck_code, upload, timestamp FROM catch_bta;");
            sqLiteDatabase.execSQL("DROP TABLE catch_bta;");
            sqLiteDatabase.execSQL("CREATE TABLE catch_bta (_id INTEGER PRIMARY KEY AUTOINCREMENT,company_id INTEGER,location_id INTEGER, record_date DATE, type TEXT, doc_number INTEGER, doc_type TEXT, truck_code TEXT, upload INTEGER DEFAULT 0, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP );");
            sqLiteDatabase.execSQL("INSERT INTO catch_bta SELECT _id, company_id, location_id, record_date, type, doc_number, doc_type, truck_code, upload, timestamp FROM catch_bta_backup;");
            sqLiteDatabase.execSQL("DROP TABLE catch_bta_backup;");
            sqLiteDatabase.execSQL("COMMIT;");
        }

        if (oldVer <= 7) {
            sqLiteDatabase.execSQL("ALTER TABLE catch_bta ADD COLUMN print_count INTEGER DEFAULT 0");
        }

        if (oldVer <= 8) {
            sqLiteDatabase.execSQL(SQL_CREATE_STANDARD_WEIGHT_TABLE);
        }

        if (oldVer <= 9) {
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_ITEM_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_IN_DETAIL_TABLE);
        }

        if (oldVer <= 10) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_in");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_in_detail");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_feed_in_detail");

            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_IN_DETAIL_TABLE);
        }

        if (oldVer <= 11) {
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_TRANSFER_TABLE);
        }

        if (oldVer <= 12) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_item");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_in");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_in_detail");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_feed_in_detail");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS feed_transfer");

            sqLiteDatabase.execSQL(SQL_CREATE_FEED_ITEM_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_IN_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_IN_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_TRANSFER_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_DISCHARGE_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_DISCHARGE_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_DISCHARGE_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_RECEIVE_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_RECEIVE_DETAIL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_FEED_RECEIVE_DETAIL_TABLE);
        }

        if (oldVer <= 13) {
            sqLiteDatabase.execSQL("ALTER TABLE catch_bta ADD COLUMN code TEXT DEFAULT ''");
        }

        if (oldVer <= 14) {
            sqLiteDatabase.execSQL("ALTER TABLE catch_bta_detail ADD COLUMN is_bt INTEGER DEFAULT 0");
            sqLiteDatabase.execSQL("ALTER TABLE temp_catch_bta_detail ADD COLUMN is_bt INTEGER DEFAULT 0");
        }
    }

    private final String SQL_CREATE_BRANCH_TABLE = "CREATE TABLE " + BranchEntry.TABLE_NAME + " (" +
            BranchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            BranchEntry.COLUMN_ERP_ID + " INTEGER," +
            BranchEntry.COLUMN_BRANCH_CODE + " TEXT NOT NULL, " +
            BranchEntry.COLUMN_BRANCH_NAME + " TEXT NOT NULL, " +
            BranchEntry.COLUMN_COMPANY_ID + " INTEGER NOT NULL " +
            "); ";

    private final String SQL_CREATE_BRANCH_HOUSE_SETUP_TABLE = "CREATE TABLE " + HouseEntry.TABLE_NAME + " (" +
            HouseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            HouseEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL," +
            HouseEntry.COLUMN_HOUSE_CODE + " INTEGER NOT NULL " +
            "); ";

    private final String SQL_CREATE_MORTALITY_TABLE = "CREATE TABLE " + MortalityEntry.TABLE_NAME + " (" +
            MortalityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MortalityEntry.COLUMN_COMPANY_ID + " INTEGER NOT NULL," +
            MortalityEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL, " +
            MortalityEntry.COLUMN_HOUSE_CODE + " INTEGER NOT NULL, " +
            MortalityEntry.COLUMN_RECORD_DATE + " DATE NOT NULL, " +
            MortalityEntry.COLUMN_M_Q + " INTEGER NOT NULL, " +
            MortalityEntry.COLUMN_R_Q + " INTEGER NOT NULL, " +
            MortalityEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            MortalityEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_CATCH_BTA_TABLE = "CREATE TABLE " + CatchBTAEntry.TABLE_NAME + " (" +
            CatchBTAEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CatchBTAEntry.COLUMN_COMPANY_ID + " INTEGER," +
            CatchBTAEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            CatchBTAEntry.COLUMN_RECORD_DATE + " DATE, " +
            CatchBTAEntry.COLUMN_TYPE + " TEXT, " +
            CatchBTAEntry.COLUMN_DOC_NUMBER + " INTEGER, " +
            CatchBTAEntry.COLUMN_DOC_TYPE + " TEXT, " +
            CatchBTAEntry.COLUMN_TRUCK_CODE + " TEXT, " +
            CatchBTAEntry.COLUMN_CODE + " TEXT, " +
            CatchBTAEntry.COLUMN_PRINT_COUNT + " INTEGER DEFAULT 0, " +
            CatchBTAEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            CatchBTAEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_CATCH_BTA_DETAIL_TABLE = "CREATE TABLE " + CatchBTADetailEntry.TABLE_NAME + " (" +
            CatchBTADetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CatchBTADetailEntry.COLUMN_CATCH_BTA_ID + " INTEGER," +
            CatchBTADetailEntry.COLUMN_WEIGHT + " REAL, " +
            CatchBTADetailEntry.COLUMN_QTY + " INTEGER, " +
            CatchBTADetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            CatchBTADetailEntry.COLUMN_CAGE_QTY + " INTEGER, " +
            CatchBTADetailEntry.COLUMN_WITH_COVER_QTY + " INTEGER DEFAULT 0, " +
            CatchBTADetailEntry.COLUMN_IS_BT + " INTEGER DEFAULT 0 " +
            "); ";

    private final String SQL_CREATE_TEMP_CATCH_BTA_TABLE = "CREATE TABLE " + TempCatchBTAEntry.TABLE_NAME + " (" +
            TempCatchBTAEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempCatchBTAEntry.COLUMN_COMPANY_ID + " INTEGER," +
            TempCatchBTAEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            TempCatchBTAEntry.COLUMN_RECORD_DATE + " DATE, " +
            TempCatchBTAEntry.COLUMN_TYPE + " TEXT, " +
            TempCatchBTAEntry.COLUMN_DOC_NUMBER + " INTEGER, " +
            TempCatchBTAEntry.COLUMN_DOC_TYPE + " TEXT, " +
            TempCatchBTAEntry.COLUMN_TRUCK_CODE + " TEXT " +
            "); ";

    private final String SQL_CREATE_TEMP_CATCH_BTA_DETAIL_TABLE = "CREATE TABLE " + TempCatchBTADetailEntry.TABLE_NAME + " (" +
            TempCatchBTADetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempCatchBTADetailEntry.COLUMN_WEIGHT + " REAL, " +
            TempCatchBTADetailEntry.COLUMN_QTY + " INTEGER, " +
            TempCatchBTADetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            TempCatchBTADetailEntry.COLUMN_CAGE_QTY + " INTEGER, " +
            TempCatchBTADetailEntry.COLUMN_WITH_COVER_QTY + " INTEGER DEFAULT 0, " +
            TempCatchBTADetailEntry.COLUMN_IS_BT + " INTEGER DEFAULT 0 " +
            "); ";

    private final String SQL_CREATE_TEMP_WEIGHT_TABLE = "CREATE TABLE " + TempWeightEntry.TABLE_NAME + " (" +
            TempWeightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempWeightEntry.COLUMN_COMPANY_ID + " INTEGER," +
            TempWeightEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            TempWeightEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            TempWeightEntry.COLUMN_DAY + " INTEGER, " +
            TempWeightEntry.COLUMN_RECORD_DATE + " DATE, " +
            TempWeightEntry.COLUMN_RECORD_TIME + " TIME, " +
            TempWeightEntry.COLUMN_FEED + " TEXT " +
            "); ";

    private final String SQL_CREATE_TEMP_WEIGHT_DETAIL_TABLE = "CREATE TABLE " + TempWeightDetailEntry.TABLE_NAME + " (" +
            TempWeightDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempWeightDetailEntry.COLUMN_SECTION + " INTEGER," +
            TempWeightDetailEntry.COLUMN_WEIGHT + " INTEGER, " +
            TempWeightDetailEntry.COLUMN_QTY + " INTEGER, " +
            TempWeightDetailEntry.COLUMN_GENDER + " TEXT " +
            "); ";

    private final String SQL_CREATE_WEIGHT_TABLE = "CREATE TABLE " + WeightEntry.TABLE_NAME + " (" +
            WeightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeightEntry.COLUMN_COMPANY_ID + " INTEGER," +
            WeightEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            WeightEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            WeightEntry.COLUMN_DAY + " INTEGER, " +
            WeightEntry.COLUMN_RECORD_DATE + " DATE, " +
            WeightEntry.COLUMN_RECORD_TIME + " TIME, " +
            WeightEntry.COLUMN_FEED + " TEXT, " +
            WeightEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            WeightEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_WEIGHT_DETAIL_TABLE = "CREATE TABLE " + WeightDetailEntry.TABLE_NAME + " (" +
            WeightDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeightDetailEntry.COLUMN_WEIGHT_ID + " INTEGER," +
            WeightDetailEntry.COLUMN_SECTION + " INTEGER," +
            WeightDetailEntry.COLUMN_WEIGHT + " INTEGER, " +
            WeightDetailEntry.COLUMN_QTY + " INTEGER, " +
            WeightDetailEntry.COLUMN_GENDER + " TEXT " +
            "); ";

    private final String SQL_CREATE_STANDARD_WEIGHT_TABLE = "CREATE TABLE " + StandardWeightEntry.TABLE_NAME + " (" +
            StandardWeightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            StandardWeightEntry.COLUMN_TYPE + " TEXT," +
            StandardWeightEntry.COLUMN_DAY + " INTEGER," +
            StandardWeightEntry.COLUMN_AVG_WEIGHT + " INTEGER " +
            "); ";

    private final String SQL_CREATE_FEED_ITEM_TABLE = "CREATE TABLE " + FeedItemEntry.TABLE_NAME + " (" +
            FeedItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedItemEntry.COLUMN_ERP_ID + " INTEGER," +
            FeedItemEntry.COLUMN_SKU_CODE + " TEXT," +
            FeedItemEntry.COLUMN_SKU_NAME + " TEXT, " +
            FeedItemEntry.COLUMN_ITEM_UOM_ID + " INTEGER" +
            "); ";

    private final String SQL_CREATE_FEED_IN_TABLE = "CREATE TABLE " + FeedInEntry.TABLE_NAME + " (" +
            FeedInEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedInEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FeedInEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            FeedInEntry.COLUMN_RECORD_DATE + " DATE, " +
            FeedInEntry.COLUMN_DOC_ID + " INTEGER, " +
            FeedInEntry.COLUMN_DOC_NUMBER + " TEXT, " +
            FeedInEntry.COLUMN_TRUCK_CODE + " TEXT, " +
            FeedInEntry.COLUMN_VARIANCE + " REAL, " +
            FeedInEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            FeedInEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_FEED_IN_DETAIL_TABLE = "CREATE TABLE " + FeedInDetailEntry.TABLE_NAME + " (" +
            FeedInDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedInDetailEntry.COLUMN_FEED_IN_ID + " INTEGER," +
            FeedInDetailEntry.COLUMN_DOC_DETAIL_ID + " INTEGER, " +
            FeedInDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            FeedInDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            FeedInDetailEntry.COLUMN_COMPARTMENT_NO + " TEXT, " +
            FeedInDetailEntry.COLUMN_QTY + " REAL, " +
            FeedInDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";

    private final String SQL_CREATE_TEMP_FEED_IN_DETAIL_TABLE = "CREATE TABLE " + TempFeedInDetailEntry.TABLE_NAME + " (" +
            TempFeedInDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempFeedInDetailEntry.COLUMN_DOC_DETAIL_ID + " INTEGER, " +
            TempFeedInDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            TempFeedInDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            TempFeedInDetailEntry.COLUMN_COMPARTMENT_NO + " TEXT, " +
            TempFeedInDetailEntry.COLUMN_QTY + " REAL, " +
            TempFeedInDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";

    private final String SQL_CREATE_FEED_TRANSFER_TABLE = "CREATE TABLE " + FeedTransferEntry.TABLE_NAME + " (" +
            FeedTransferEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedTransferEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FeedTransferEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            FeedTransferEntry.COLUMN_RECORD_DATE + " DATE, " +
            FeedTransferEntry.COLUMN_RUNNING_NO + " TEXT, " +
            FeedTransferEntry.COLUMN_DISCHARGE_HOUSE + " INTEGER, " +
            FeedTransferEntry.COLUMN_RECEIVE_HOUSE + " INTEGER, " +
            FeedTransferEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            FeedTransferEntry.COLUMN_WEIGHT + " REAL, " +
            FeedTransferEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            FeedTransferEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_FEED_DISCHARGE_TABLE = "CREATE TABLE " + FeedDischargeEntry.TABLE_NAME + " (" +
            FeedDischargeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedDischargeEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FeedDischargeEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            FeedDischargeEntry.COLUMN_RECORD_DATE + " DATE, " +
            FeedDischargeEntry.COLUMN_DISCHARGE_CODE + " TEXT, " +
            FeedDischargeEntry.COLUMN_RUNNING_NO + " TEXT, " +
            FeedDischargeEntry.COLUMN_TRUCK_CODE + " TEXT, " +
            FeedDischargeEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            FeedDischargeEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_FEED_DISCHARGE_DETAIL_TABLE = "CREATE TABLE " + FeedDischargeDetailEntry.TABLE_NAME + " (" +
            FeedDischargeDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedDischargeDetailEntry.COLUMN_FEED_DISCHARGE_ID + " INTEGER," +
            FeedDischargeDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            FeedDischargeDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";

    private final String SQL_CREATE_TEMP_FEED_DISCHARGE_DETAIL_TABLE = "CREATE TABLE " + TempFeedDischargeDetailEntry.TABLE_NAME + " (" +
            TempFeedDischargeDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempFeedDischargeDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            TempFeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            TempFeedDischargeDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";

    private final String SQL_CREATE_FEED_RECEIVE_TABLE = "CREATE TABLE " + FeedReceiveEntry.TABLE_NAME + " (" +
            FeedReceiveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedReceiveEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FeedReceiveEntry.COLUMN_LOCATION_ID + " INTEGER, " +
            FeedReceiveEntry.COLUMN_RECORD_DATE + " DATE, " +
            FeedReceiveEntry.COLUMN_DISCHARGE_CODE + " TEXT, " +
            FeedReceiveEntry.COLUMN_RUNNING_NO + " TEXT, " +
            FeedReceiveEntry.COLUMN_TRUCK_CODE + " TEXT, " +
            FeedReceiveEntry.COLUMN_VARIANCE + " REAL, " +
            FeedReceiveEntry.COLUMN_UPLOAD + " INTEGER DEFAULT 0, " +
            FeedReceiveEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            "); ";

    private final String SQL_CREATE_FEED_RECEIVE_DETAIL_TABLE = "CREATE TABLE " + FeedReceiveDetailEntry.TABLE_NAME + " (" +
            FeedReceiveDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FeedReceiveDetailEntry.COLUMN_FEED_RECEIVE_ID + " INTEGER," +
            FeedReceiveDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            FeedReceiveDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";

    private final String SQL_CREATE_TEMP_FEED_RECEIVE_DETAIL_TABLE = "CREATE TABLE " + TempFeedReceiveDetailEntry.TABLE_NAME + " (" +
            TempFeedReceiveDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TempFeedReceiveDetailEntry.COLUMN_HOUSE_CODE + " INTEGER, " +
            TempFeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID + " INTEGER, " +
            TempFeedReceiveDetailEntry.COLUMN_WEIGHT + " REAL " +
            "); ";
}






















