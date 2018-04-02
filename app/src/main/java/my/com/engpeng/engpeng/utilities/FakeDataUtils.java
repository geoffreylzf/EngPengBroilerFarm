package my.com.engpeng.engpeng.utilities;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 4/1/2018.
 */

public class FakeDataUtils {

    public static void insertFakeData(SQLiteDatabase db) {
        if (db == null) {
            return;
        }

        List<ContentValues> branchList = new ArrayList<ContentValues>();

        branchList.add(cvBranch(1, "BN", "Bumi Nian Sdn Bhd", 0));
        branchList.add(cvBranch(2, "EP", "Eden Perfect Sdn Bhd", 0));
        branchList.add(cvBranch(3, "CS", "Eng Peng Cold Storage Sdn Bhd", 0));

        branchList.add(cvBranch(88, "K-GSTR-BN", "KK-General Store-BN", 1));
        branchList.add(cvBranch(166, "BN-HQ", "BN-Head Quarter", 1));
        branchList.add(cvBranch(381, "GEN", "BS-GENERAL", 1));

        branchList.add(cvBranch(89, "EP-HQ", "EP-HQ", 2));
        branchList.add(cvBranch(171, "K-HQ-EP", "KK-EP Head Quarter", 2));
        branchList.add(cvBranch(461, "GEN", "BS-GENERAL", 2));

        branchList.add(cvBranch(22, "K-BTYa", "KK-BantayanA", 3));
        branchList.add(cvBranch(23, "K-BTYb", "KK-BantayanB", 3));
        branchList.add(cvBranch(34, "K-SLTa", "KK-SaluteA", 3));

        List<ContentValues> bhsList = new ArrayList<ContentValues>();
        bhsList.add(cvBranchHouseSetup(22, 2));
        bhsList.add(cvBranchHouseSetup(22, 3));
        bhsList.add(cvBranchHouseSetup(22, 4));
        bhsList.add(cvBranchHouseSetup(22, 5));
        bhsList.add(cvBranchHouseSetup(22, 7));
        bhsList.add(cvBranchHouseSetup(22, 8));
        bhsList.add(cvBranchHouseSetup(22, 9));
        bhsList.add(cvBranchHouseSetup(22, 10));
        bhsList.add(cvBranchHouseSetup(22, 11));
        bhsList.add(cvBranchHouseSetup(22, 14));
        bhsList.add(cvBranchHouseSetup(22, 15));
        bhsList.add(cvBranchHouseSetup(22, 18));
        bhsList.add(cvBranchHouseSetup(22, 20));
        bhsList.add(cvBranchHouseSetup(22, 19));

        bhsList.add(cvBranchHouseSetup(23, 21));
        bhsList.add(cvBranchHouseSetup(23, 22));
        bhsList.add(cvBranchHouseSetup(23, 23));
        bhsList.add(cvBranchHouseSetup(23, 24));
        bhsList.add(cvBranchHouseSetup(23, 25));
        bhsList.add(cvBranchHouseSetup(23, 26));
        bhsList.add(cvBranchHouseSetup(23, 29));

        bhsList.add(cvBranchHouseSetup(34, 1));
        bhsList.add(cvBranchHouseSetup(34, 2));
        bhsList.add(cvBranchHouseSetup(34, 3));
        bhsList.add(cvBranchHouseSetup(34, 4));
        bhsList.add(cvBranchHouseSetup(34, 5));

        try {
            db.beginTransaction();

            DatabaseUtils.clearSystemData(db);

            for (ContentValues c : branchList) {
                db.insert(BranchEntry.TABLE_NAME, null, c);
            }

            //Branch House Setup
            for (ContentValues c : bhsList) {
                db.insert(HouseEntry.TABLE_NAME, null, c);
            }


            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public static ContentValues cvBranch(int erp_id, String branch_code, String branch_name, int company_id){
        ContentValues cv = new ContentValues();
        cv.put(BranchEntry.COLUMN_ERP_ID, erp_id);
        cv.put(BranchEntry.COLUMN_BRANCH_CODE, branch_code);
        cv.put(BranchEntry.COLUMN_BRANCH_NAME, branch_name);
        cv.put(BranchEntry.COLUMN_COMPANY_ID, company_id);
        return cv;
    }

    public static ContentValues cvBranchHouseSetup(int location_id, int house_code){
        ContentValues cv = new ContentValues();
        cv.put(HouseEntry.COLUMN_LOCATION_ID, location_id);
        cv.put(HouseEntry.COLUMN_HOUSE_CODE, house_code);
        return cv;
    }



}
