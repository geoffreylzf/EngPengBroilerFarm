package my.com.engpeng.engpeng.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.controller.FeedDischargeController;
import my.com.engpeng.engpeng.controller.FeedDischargeDetailController;
import my.com.engpeng.engpeng.controller.FeedInController;
import my.com.engpeng.engpeng.controller.FeedInDetailController;
import my.com.engpeng.engpeng.controller.FeedItemController;
import my.com.engpeng.engpeng.controller.FeedReceiveController;
import my.com.engpeng.engpeng.controller.FeedReceiveDetailController;
import my.com.engpeng.engpeng.controller.FeedTransferController;
import my.com.engpeng.engpeng.controller.StandardWeightController;
import my.com.engpeng.engpeng.controller.WeightController;
import my.com.engpeng.engpeng.controller.WeightDetailController;

import static my.com.engpeng.engpeng.Global.sUsername;
import static my.com.engpeng.engpeng.data.EngPengContract.*;

/**
 * Created by Admin on 28/2/2018.
 */

public class PrintUtils {

    private static final String PRINT_END = "\n\n\n\n\n";
    private static final String PRINT_SEPERATOR = "---------------------------------------------";
    private static final String PRINT_HALF_SEPERATOR = "----------------------";

    private static final double WEIGHT_COVER_WITH_CAGE = 8;
    private static final double WEIGHT_COVER_WITHOUT_CAGE = 7.6;

    public static String printCatchBTA(Context context, SQLiteDatabase db, long catch_bta_id) {

        String text = "";

        Cursor cursorCatchBTA = CatchBTAController.getById(db, catch_bta_id);
        cursorCatchBTA.moveToFirst();
        String record_date = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_RECORD_DATE));
        String doc_number = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_DOC_NUMBER));
        String doc_type = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_DOC_TYPE));
        String type = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_TYPE));
        if (type.equals("B")) {
            type = "C";
        }

        String truck_code = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_TRUCK_CODE));
        String code = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_CODE));
        String fasting_time = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_FASTING_TIME));
        String catch_team = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_CATCH_TEAM));
        int company_id = cursorCatchBTA.getInt(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_COMPANY_ID));
        int location_id = cursorCatchBTA.getInt(cursorCatchBTA.getColumnIndex(CatchBTAEntry.COLUMN_LOCATION_ID));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_code = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));
        String location_name = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Bill Timbangan Ayam");
        text += formatLine("Date: " + record_date);
        text += formatLine("Document: " + doc_type + " - " + doc_number);
        text += formatLine("Truck Code : " + truck_code);
        text += formatLine("Code : " + code);
        text += formatLine("Masa Puasa : " + fasting_time);
        text += formatLine("Kumpulan : " + catch_team);
        text += formatLine("");
        text += formatLine("Umur :");
        text += formatLine("Ayam :______________________");
        text += formatLine("");
        text += formatLine(halfLine("Stock Description") + "|" + halfLine("Stock Description"));
        text += formatLine(halfLine("Live Bird - Grade " + type) + "|" + halfLine("Live Bird - Grade " + type));
        text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));

        int[] houseArr = CatchBTADetailController.getHouseCodeByCatchBTAId(db, catch_bta_id);

        int ttl_qty = 0;
        double ttl_wgt = 0;
        int ttl_cage = 0;
        int ttl_cage_with_cover = 0;
        int ttl_cage_without_cover = 0;
        double ttl_wgt_cage_with_cover = 0;
        double ttl_wgt_cage_without_cover = 0;

        for (int i = 0; i < houseArr.length; i++) {
            int house_code = houseArr[i];
            String house_str = formatNumber(2, house_code);

            text += formatLine(halfLine("Kdg#: " + location_code + " " + house_str) + "|" + halfLine("Kdg#: " + location_code + " " + house_str));
            text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));
            text += formatLine(btaTableHeader() + "|" + btaTableHeader());

            Cursor cursorCatchBTADetail = CatchBTADetailController.getAllByCatchBTAIdHouseCode(db, catch_bta_id, house_code);
            int row = cursorCatchBTADetail.getCount();
            double halfRow = (double) row / 2.0;

            int firstColumnRow = 0, secondColumnRow = 0;
            boolean isOdd = false;
            if (row % 2 == 1) {
                isOdd = true;
            }

            if (isOdd) {
                firstColumnRow = (int) (Math.ceil(halfRow));
                secondColumnRow = (int) (Math.floor(halfRow));
            } else {
                firstColumnRow = (int) halfRow;
                secondColumnRow = (int) halfRow;
            }

            double weight_per_house = 0;
            int qty_per_house = 0;

            for (int x = 0; x < firstColumnRow; x++) {
                cursorCatchBTADetail.moveToPosition(x);
                String num = formatNumber(3, x + 1);
                double weight = cursorCatchBTADetail.getDouble(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_WEIGHT));
                String qty = cursorCatchBTADetail.getString(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_QTY));
                int cage_qty = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_CAGE_QTY));

                int with_cover_qty = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
                int without_cover_qty = cage_qty - with_cover_qty;

                ttl_qty += Integer.parseInt(qty);
                ttl_wgt += weight;

                weight_per_house += weight;
                qty_per_house += Integer.parseInt(qty);

                ttl_cage += cage_qty;
                ttl_cage_with_cover += with_cover_qty;
                ttl_wgt_cage_with_cover += with_cover_qty * WEIGHT_COVER_WITH_CAGE;
                ttl_cage_without_cover += without_cover_qty;
                ttl_wgt_cage_without_cover += without_cover_qty * WEIGHT_COVER_WITHOUT_CAGE;

                String leftColumn = btaTableRow(num, weight, qty, with_cover_qty);

                String rightColumn = halfLine("");

                if (x + firstColumnRow < row) {
                    cursorCatchBTADetail.moveToPosition(x + firstColumnRow);
                    String num2 = formatNumber(3, x + 1 + firstColumnRow);
                    double weight2 = cursorCatchBTADetail.getDouble(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_WEIGHT));
                    String qty2 = cursorCatchBTADetail.getString(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_QTY));

                    int cage_qty2 = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_CAGE_QTY));
                    int with_cover_qty2 = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(CatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
                    int without_cover_qty2 = cage_qty2 - with_cover_qty2;

                    ttl_qty += Integer.parseInt(qty2);
                    ttl_wgt += weight2;

                    weight_per_house += weight2;
                    qty_per_house += Integer.parseInt(qty2);

                    ttl_cage += cage_qty2;
                    ttl_cage_with_cover += with_cover_qty2;
                    ttl_wgt_cage_with_cover += with_cover_qty2 * WEIGHT_COVER_WITH_CAGE;
                    ttl_cage_without_cover += without_cover_qty2;
                    ttl_wgt_cage_without_cover += without_cover_qty2 * WEIGHT_COVER_WITHOUT_CAGE;

                    rightColumn = btaTableRow(num2, weight2, qty2, with_cover_qty2);
                }


                text += formatLine(leftColumn + "|" + rightColumn);
            }

            text += formatLine(halfLine("") + "|" + halfLine(""));
            text += formatLine(halfLine(" WGT: " + String.format("%.2f", weight_per_house) + " kg") + "|" + halfLine(" QTY: " + qty_per_house + " heads"));
            text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));
        }
        text += formatLine("");

        text += formatLine(halfLine("Jumlah Ayam:") + String.format("%15d%7s", ttl_qty, "heads"));
        text += formatLine(halfLine("Jumlah Berat Kasar:") + String.format("%15.2f%7s", ttl_wgt, "kg   "));
        text += formatLine("");
        text += formatLine(halfLine("Jumlah Kurungan:") + String.format("%15d%7s", ttl_cage, "qty  "));
        text += formatLine(halfLine("Ada Tutupan:") + String.format("%15.2f%7s", ttl_wgt_cage_with_cover, "kg   "));
        text += formatLine(halfLine("Tanpa Tutupan:") + String.format("%15.2f%7s", ttl_wgt_cage_without_cover, "kg   "));
        text += formatLine(halfLine("Jumlah Berat Kurungan:") + String.format("%15.2f%7s", ttl_wgt_cage_with_cover + ttl_wgt_cage_without_cover, "kg   "));
        text += formatLine("");
        double net_wgt = ttl_wgt - (ttl_wgt_cage_with_cover + ttl_wgt_cage_without_cover);
        double avg_wgt = net_wgt / ttl_qty;
        text += formatLine(halfLine("Jumlah Berat Bersih:") + String.format("%15.2f%7s", net_wgt, "kg   "));
        text += formatLine(halfLine("Purata Seekor:") + String.format("%15.2f%7s", avg_wgt, "kg   "));
        text += formatLine("");
        text += formatLine("Printed by: " + sUsername);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

        Date currentTime = Calendar.getInstance().getTime();

        text += formatLine("Date: " + sdf.format(currentTime));
        text += formatLine("Time: " + sdfTime.format(currentTime));
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            text += formatLine("Ver : " + pInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("   Mandor/Supervisor  ") + halfLine("        Driver        "));
        text += formatLine("                  --END--                   ");

        return text;
    }

    private static String btaTableHeader() {
        return "  #    Weight  Qty  C ";
    }

    private static String btaTableRow(String num, double weight, String qty, int cover) {
        return String.format(" %3s %8.02f  %3s  %1s ", num, weight, qty, cover == (0) ? " " : String.valueOf(cover));
    }

    private static String formatLine(String line) {
        if (line.length() > 45) {
            line = line.substring(0, 45);
        }
        return String.format("%-42s", line) + "\n";
    }

    private static String halfLine(String halfLine) {
        if (halfLine.length() > 22) {
            halfLine = halfLine.substring(0, 22);
        }
        return String.format("%-22s", halfLine);
    }

    private static String formatNumber(int length, int house_code) {
        return String.format("%0" + length + "d", house_code);

    }

    public static String printWeight(SQLiteDatabase db, long weight_id) {
        String text = "";

        Cursor cursorWeight = WeightController.getById(db, weight_id);
        cursorWeight.moveToFirst();

        String record_date = cursorWeight.getString(cursorWeight.getColumnIndex(WeightEntry.COLUMN_RECORD_DATE));
        String record_time = cursorWeight.getString(cursorWeight.getColumnIndex(WeightEntry.COLUMN_RECORD_TIME));
        String feed = cursorWeight.getString(cursorWeight.getColumnIndex(WeightEntry.COLUMN_FEED));
        int day = cursorWeight.getInt(cursorWeight.getColumnIndex(WeightEntry.COLUMN_DAY));
        int house_code = cursorWeight.getInt(cursorWeight.getColumnIndex(WeightEntry.COLUMN_HOUSE_CODE));

        int company_id = cursorWeight.getInt(cursorWeight.getColumnIndex(WeightEntry.COLUMN_COMPANY_ID));
        int location_id = cursorWeight.getInt(cursorWeight.getColumnIndex(WeightEntry.COLUMN_LOCATION_ID));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));
        String location_name = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Body Weight Receipt");
        text += formatLine("Location: " + location_code);
        text += formatLine("House: " + house_code);
        text += formatLine(halfLine("Date: " + record_date) + " " + halfLine("Time: " + record_time));
        text += formatLine("Feed: " + feed);
        text += formatLine("Day: " + day);
        text += formatLine("");

        text += formatLine(halfLine(" Body Weight Detail") + "|" + halfLine(" Body Weight Detail"));
        text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));

        int[] sectionArr = WeightDetailController.getSectionByWeightId(db, weight_id);

        for (int i = 0; i < sectionArr.length; i++) {
            int section = sectionArr[i];

            text += formatLine(halfLine(" Section: " + section) + "|" + halfLine(" Section: " + section));
            text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));
            text += formatLine(weightTableHeader() + "|" + weightTableHeader());

            Cursor cursorWeightDetail = WeightDetailController.getAllByWeightIdSection(db, weight_id, section);
            int row = cursorWeightDetail.getCount();
            double halfRow = (double) row / 2.0;

            int firstColumnRow = 0, secondColumnRow = 0;
            boolean isOdd = false;
            if (row % 2 == 1) {
                isOdd = true;
            }

            if (isOdd) {
                firstColumnRow = (int) (Math.ceil(halfRow));
                secondColumnRow = (int) (Math.floor(halfRow));
            } else {
                firstColumnRow = (int) halfRow;
                secondColumnRow = (int) halfRow;
            }

            int weight_per_section = 0;
            int qty_per_section = 0;

            for (int x = 0; x < firstColumnRow; x++) {
                cursorWeightDetail.moveToPosition(x);
                String num = formatNumber(3, x + 1);

                int weight = cursorWeightDetail.getInt(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT));
                int qty = cursorWeightDetail.getInt(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_QTY));
                String gender = cursorWeightDetail.getString(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_GENDER));

                weight_per_section += weight;
                qty_per_section += qty;

                String leftColumn = weightTableRow(num, weight, qty, gender);

                String rightColumn = halfLine("");

                if (x + firstColumnRow < row) {
                    cursorWeightDetail.moveToPosition(x + firstColumnRow);
                    String num2 = formatNumber(3, x + 1 + firstColumnRow);
                    int weight2 = cursorWeightDetail.getInt(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_WEIGHT));
                    int qty2 = cursorWeightDetail.getInt(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_QTY));
                    String gender2 = cursorWeightDetail.getString(cursorWeightDetail.getColumnIndex(WeightDetailEntry.COLUMN_GENDER));

                    weight_per_section += weight2;
                    qty_per_section += qty2;

                    rightColumn = weightTableRow(num2, weight2, qty2, gender2);
                }

                text += formatLine(leftColumn + "|" + rightColumn);
            }

            text += formatLine(halfLine("") + "|" + halfLine(""));
            text += formatLine(halfLine(" WGT: " + weight_per_section + " gram") + "|" + halfLine(" QTY: " + qty_per_section + " heads"));
            text += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR));
        }

        text += formatLine("");
        text += formatLine("-------------------Summary-------------------");
        text += formatLine(String.format("%7s %11s %5s %9s %9s ", "   Type", "Wgt(g)", "Qty", "Avg(g)", "S.Avg(g)"));

        int maleTtlWgt = WeightDetailController.getTotalWgtByGenderWeightId(db, weight_id, "M");
        int maleTtlQty = WeightDetailController.getTotalQtyByGenderWeightId(db, weight_id, "M");
        double maleAvg = 0;
        if (maleTtlQty != 0) {
            maleAvg = (double) maleTtlWgt / maleTtlQty;
        }
        int std_weight_male = StandardWeightController.getAvgWeightByTypeDay(db, StandardWeightController.TYPE_MALE, day);

        text += formatLine(String.format(Locale.getDefault(), "%-7s %11d %5d %9.2f %9.2f ",
                "   Male",
                maleTtlWgt,
                maleTtlQty,
                maleAvg,
                (float) std_weight_male));

        int femaleTtlWgt = WeightDetailController.getTotalWgtByGenderWeightId(db, weight_id, "F");
        int femaleTtlQty = WeightDetailController.getTotalQtyByGenderWeightId(db, weight_id, "F");
        double femaleAvg = 0;
        if (femaleTtlQty != 0) {
            femaleAvg = (double) femaleTtlWgt / femaleTtlQty;
        }
        int std_weight_female = StandardWeightController.getAvgWeightByTypeDay(db, StandardWeightController.TYPE_FEMALE, day);

        text += formatLine(String.format(Locale.getDefault(), "%-7s %11d %5d %9.2f %9.2f ",
                " Female",
                femaleTtlWgt,
                femaleTtlQty,
                femaleAvg,
                (float) std_weight_female));

        int oTtlWgt = WeightDetailController.getTotalWgtByWeightId(db, weight_id);
        int oTtlQty = WeightDetailController.getTotalQtyByWeightId(db, weight_id);
        double oAvg = 0;
        if (oTtlQty != 0) {
            oAvg = (double) oTtlWgt / oTtlQty;
        }
        int std_weight_overall = StandardWeightController.getAvgWeightByTypeDay(db, StandardWeightController.TYPE_OVERALL, day);

        text += formatLine(String.format(Locale.getDefault(), "%-7s %11d %5d %9.2f %9.2f ",
                "Overall",
                oTtlWgt,
                oTtlQty,
                oAvg,
                (float) std_weight_overall));

        text += formatLine(PRINT_SEPERATOR);
        text += formatLine("Printed by: " + sUsername);
        text += formatLine("");
        text += formatLine("                   --END--                   ");

        return text;
    }

    private static String weightTableHeader() {
        return "  #    Weight  Qty  G ";
    }

    private static String weightTableRow(String num, int weight, int qty, String gender) {
        return String.format(" %3s %8d  %3d  %1s ", num, weight, qty, gender);
    }

    public static String printFeedIn(SQLiteDatabase db, long feed_in_id) {
        String text = "";

        Cursor cursorFeedIn = FeedInController.getById(db, feed_in_id);
        cursorFeedIn.moveToFirst();

        int company_id = cursorFeedIn.getInt(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_COMPANY_ID));
        int location_id = cursorFeedIn.getInt(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_LOCATION_ID));
        String record_date = cursorFeedIn.getString(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_RECORD_DATE));
        Long doc_id = cursorFeedIn.getLong(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_DOC_ID));
        String doc_number = cursorFeedIn.getString(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_DOC_NUMBER));
        String truck_code = cursorFeedIn.getString(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_TRUCK_CODE));
        double variance = cursorFeedIn.getDouble(cursorFeedIn.getColumnIndex(FeedInEntry.COLUMN_VARIANCE));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("GRN - Feed Receipt");
        text += formatLine("Location: " + location_code);
        text += formatLine("Date: " + record_date);
        text += formatLine("Doc: " + doc_number);
        text += formatLine("ID: " + doc_id);
        text += formatLine("Truck Code: " + truck_code);

        Cursor cursorGroup = FeedInDetailController.getItemPackingIdByFeedInId(db, feed_in_id);

        while (cursorGroup.moveToNext()) {
            int item_packing_id = cursorGroup.getInt(cursorGroup.getColumnIndex(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID));
            Cursor cursorFeedItem = FeedItemController.getByErpId(db, item_packing_id);
            String sku_code = "";
            String sku_name = "";
            if (cursorFeedItem.moveToFirst()) {
                sku_code = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                sku_name = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
            } else {
                sku_code = "New Feed";
                sku_name = "ITEM_PACKING_ID: " + item_packing_id;
            }

            text += formatLine("");
            text += formatLine("Feed Code: " + sku_code);
            text += formatLine("Feed Name: " + sku_name);
            text += formatLine(PRINT_SEPERATOR);
            text += formatLine(String.format("  House  Compartment  Quantity  Weight(KG)"));

            double ttl_qty = 0;
            double ttl_weight = 0;
            Cursor cursorDetail = FeedInDetailController.getAllByFeedInIdItemPackingId(db, feed_in_id, item_packing_id);
            while (cursorDetail.moveToNext()) {
                String house_code = cursorDetail.getString(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_HOUSE_CODE));
                String compartment_no = cursorDetail.getString(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_COMPARTMENT_NO));
                Double qty = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_QTY));
                Double weight = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_WEIGHT));

                ttl_qty += qty;
                ttl_weight += weight;

                text += formatLine(String.format("  %4s        %s       %8.3f   %9.2f", house_code, compartment_no, qty, weight));
            }
            text += formatLine(PRINT_SEPERATOR);
            text += formatLine(String.format("Total:               %8.3f   %9.2f", ttl_qty, ttl_weight));
            text += formatLine(String.format("                     =========  =========="));
        }

        /*Cursor cursorDetail = FeedInDetailController.getAllByFeedInIdOrderByItemPackingId(db, feed_in_id);

        int current_item_packing_id = 0;
        while (cursorDetail.moveToNext()) {
            int item_packing_id = cursorDetail.getInt(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_ITEM_PACKING_ID));

            String house_code = cursorDetail.getString(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_HOUSE_CODE));
            String compartment_no = cursorDetail.getString(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_COMPARTMENT_NO));
            Double qty = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_QTY));
            Double weight = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedInDetailEntry.COLUMN_WEIGHT));

            Cursor cursorFeedItem = FeedItemController.getByErpId(db, item_packing_id);

            String sku_code = "";
            String sku_name = "";
            if (cursorFeedItem.moveToFirst()) {
                sku_code = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                sku_name = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
            }else{
                sku_code = "New Feed";
                sku_name = "ITEM_PACKING_ID: " + item_packing_id;
            }

            if (current_item_packing_id != item_packing_id) {
                if (current_item_packing_id != 0) {
                    text += formatLine(PRINT_SEPERATOR);
                }
                text += formatLine("");
                text += formatLine("Feed Code: " + sku_code);
                text += formatLine("Feed Name: " + sku_name);

                Cursor cursorTotal = FeedInDetailController.getTotalQtyWeightByFeedInIdItemPackingId(db, feed_in_id, item_packing_id);
                cursorTotal.moveToFirst();

                Double ttlQty = cursorTotal.getDouble(cursorTotal.getColumnIndex(FeedInDetailEntry.COLUMN_QTY));
                Double ttlWeight = cursorTotal.getDouble(cursorTotal.getColumnIndex(FeedInDetailEntry.COLUMN_WEIGHT));

                text += formatLine("Total Qty: " + ttlQty);
                text += formatLine("Total Weight (KG): " + ttlWeight);

                text += formatLine(PRINT_SEPERATOR);
                text += formatLine(String.format("  House  Compartment  Quantity  Weight(KG)"));
            }

            text += formatLine(String.format("  %4s        %s       %8.3f   %9.2f", house_code, compartment_no, qty, weight));

            if (current_item_packing_id != item_packing_id) {

            }

            current_item_packing_id = item_packing_id;
        }
        text += formatLine(PRINT_SEPERATOR);*/

        text += formatLine("");
        String variance_str = variance + " KG";
        if (variance > 0) {
            variance_str = "+" + variance_str;
        }
        text += formatLine("Variance : " + variance_str);
        text += formatLine("Printed by: " + sUsername);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

        Date currentTime = Calendar.getInstance().getTime();

        text += formatLine("Date: " + sdf.format(currentTime));
        text += formatLine("Time: " + sdfTime.format(currentTime));
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("   Mandor/Supervisor  ") + halfLine("        Driver        "));
        text += formatLine("                  --END--                   ");


        return text;
    }

    public static String printFeedDischarge(SQLiteDatabase db, long feed_discharge_id) {
        String text = "";

        Cursor cursorFeedDischarge = FeedDischargeController.getById(db, feed_discharge_id);
        cursorFeedDischarge.moveToFirst();

        int company_id = cursorFeedDischarge.getInt(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_COMPANY_ID));
        int location_id = cursorFeedDischarge.getInt(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_LOCATION_ID));
        String record_date = cursorFeedDischarge.getString(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_RECORD_DATE));
        String discharge_code = cursorFeedDischarge.getString(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_DISCHARGE_CODE));
        String running_no = cursorFeedDischarge.getString(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_RUNNING_NO));
        String truck_code = cursorFeedDischarge.getString(cursorFeedDischarge.getColumnIndex(FeedDischargeEntry.COLUMN_TRUCK_CODE));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Inter Farm Transfer-Out");
        text += formatLine("Location: " + location_code);
        text += formatLine("Date: " + record_date);
        text += formatLine("Discharge Code: " + discharge_code);
        text += formatLine("Running No: " + running_no);
        text += formatLine("Truck Code: " + truck_code);

        Cursor cursorDetail = FeedDischargeDetailController.getAllByFeedDischargeIdOrderByItemPackingId(db, feed_discharge_id);

        int current_item_packing_id = 0;
        while (cursorDetail.moveToNext()) {
            int item_packing_id = cursorDetail.getInt(cursorDetail.getColumnIndex(FeedDischargeDetailEntry.COLUMN_ITEM_PACKING_ID));

            String house_code = cursorDetail.getString(cursorDetail.getColumnIndex(FeedDischargeDetailEntry.COLUMN_HOUSE_CODE));
            Double weight = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedDischargeDetailEntry.COLUMN_WEIGHT));

            Cursor cursorFeedItem = FeedItemController.getByErpId(db, item_packing_id);

            String sku_code = "New Feed";
            String sku_name = "ITEM_PACKING_ID: " + item_packing_id;
            if (cursorFeedItem.moveToFirst()) {
                sku_code = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                sku_name = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
            }
            if (current_item_packing_id != item_packing_id) {
                if (current_item_packing_id != 0) {
                    text += formatLine(PRINT_SEPERATOR);
                }
                text += formatLine("");
                text += formatLine("Feed Code: " + sku_code);
                text += formatLine("Feed Name: " + sku_name);

                Double ttlWeight = FeedDischargeDetailController.getTotalWeightByFeedDischargeIdItemPackingId(db, feed_discharge_id, item_packing_id);
                text += formatLine("Total Weight (KG): " + ttlWeight);
                text += formatLine(PRINT_HALF_SEPERATOR);
                text += formatLine(String.format("  House  Weight(KG) "));
            }

            text += formatLine(String.format("  %4s    %9.2f ", house_code, weight));

            current_item_packing_id = item_packing_id;

        }

        text += formatLine(PRINT_HALF_SEPERATOR);

        text += formatLine("");
        text += formatLine("Printed by: " + sUsername);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

        Date currentTime = Calendar.getInstance().getTime();

        text += formatLine("Date: " + sdf.format(currentTime));
        text += formatLine("Time: " + sdfTime.format(currentTime));
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("   Mandor/Supervisor  ") + halfLine("        Driver        "));
        text += formatLine("                  --END--                   ");

        return text;
    }

    public static String printFeedReceive(SQLiteDatabase db, long feed_receive_id) {
        String text = "";

        Cursor cursorFeedReceive = FeedReceiveController.getById(db, feed_receive_id);
        cursorFeedReceive.moveToFirst();

        int company_id = cursorFeedReceive.getInt(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_COMPANY_ID));
        int location_id = cursorFeedReceive.getInt(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_LOCATION_ID));
        String record_date = cursorFeedReceive.getString(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_RECORD_DATE));
        String discharge_code = cursorFeedReceive.getString(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_DISCHARGE_CODE));
        String running_no = cursorFeedReceive.getString(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_RUNNING_NO));
        String truck_code = cursorFeedReceive.getString(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_TRUCK_CODE));
        double variance = cursorFeedReceive.getDouble(cursorFeedReceive.getColumnIndex(FeedReceiveEntry.COLUMN_VARIANCE));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Inter Farm Transfer-In");
        text += formatLine("Location: " + location_code);
        text += formatLine("Date: " + record_date);
        text += formatLine("Discharge Code: " + discharge_code);
        text += formatLine("Running No: " + running_no);
        text += formatLine("Truck Code: " + truck_code);

        Cursor cursorDetail = FeedReceiveDetailController.getAllByFeedReceiveIdOrderByItemPackingId(db, feed_receive_id);

        int current_item_packing_id = 0;
        while (cursorDetail.moveToNext()) {
            int item_packing_id = cursorDetail.getInt(cursorDetail.getColumnIndex(FeedReceiveDetailEntry.COLUMN_ITEM_PACKING_ID));

            String house_code = cursorDetail.getString(cursorDetail.getColumnIndex(FeedReceiveDetailEntry.COLUMN_HOUSE_CODE));
            Double weight = cursorDetail.getDouble(cursorDetail.getColumnIndex(FeedReceiveDetailEntry.COLUMN_WEIGHT));

            Cursor cursorFeedItem = FeedItemController.getByErpId(db, item_packing_id);

            String sku_code = "New Feed";
            String sku_name = "ITEM_PACKING_ID: " + item_packing_id;
            if (cursorFeedItem.moveToFirst()) {
                sku_code = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
                sku_name = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
            }
            if (current_item_packing_id != item_packing_id) {
                if (current_item_packing_id != 0) {
                    text += formatLine(PRINT_SEPERATOR);
                }
                text += formatLine("");
                text += formatLine("Feed Code: " + sku_code);
                text += formatLine("Feed Name: " + sku_name);

                Double ttlWeight = FeedReceiveDetailController.getTotalWeightByFeedReceiveIdItemPackingId(db, feed_receive_id, item_packing_id);
                text += formatLine("Total Weight (KG): " + ttlWeight);
                text += formatLine(PRINT_HALF_SEPERATOR);
                text += formatLine(String.format("  House  Weight(KG) "));
            }

            text += formatLine(String.format("  %4s    %9.2f ", house_code, weight));

            current_item_packing_id = item_packing_id;

        }

        text += formatLine(PRINT_HALF_SEPERATOR);

        text += formatLine("");
        String variance_str = variance + " KG";
        if (variance > 0) {
            variance_str = "+" + variance_str;
        }
        text += formatLine("Variance : " + variance_str);
        text += formatLine("Printed by: " + sUsername);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);

        Date currentTime = Calendar.getInstance().getTime();

        text += formatLine("Date: " + sdf.format(currentTime));
        text += formatLine("Time: " + sdfTime.format(currentTime));
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("   Mandor/Supervisor  ") + halfLine("        Driver        "));
        text += formatLine("                  --END--                   ");

        return text;
    }

    public static String printFeedTransfer(SQLiteDatabase db, long feed_transfer_id) {
        String text = "";

        Cursor cursorFeedTransfer = FeedTransferController.getById(db, feed_transfer_id);
        cursorFeedTransfer.moveToFirst();

        int company_id = cursorFeedTransfer.getInt(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_COMPANY_ID));
        int location_id = cursorFeedTransfer.getInt(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_LOCATION_ID));
        String record_date = cursorFeedTransfer.getString(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_RECORD_DATE));
        String running_no = cursorFeedTransfer.getString(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_RUNNING_NO));
        int discharge_house = cursorFeedTransfer.getInt(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_DISCHARGE_HOUSE));
        int receive_house = cursorFeedTransfer.getInt(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_RECEIVE_HOUSE));
        int item_packing_id = cursorFeedTransfer.getInt(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_ITEM_PACKING_ID));
        double weight = cursorFeedTransfer.getDouble(cursorFeedTransfer.getColumnIndex(FeedTransferEntry.COLUMN_WEIGHT));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(BranchEntry.COLUMN_BRANCH_CODE));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Inter House Transfer");
        text += formatLine("Location: " + location_code);
        text += formatLine("Date: " + record_date);
        text += formatLine("Running No: " + running_no);
        text += formatLine("");
        text += formatLine("Discharge House: #" + discharge_house);
        text += formatLine("Receive House: #" + receive_house);

        Cursor cursorFeedItem = FeedItemController.getByErpId(db, item_packing_id);

        String sku_code = "New Feed";
        String sku_name = "ITEM_PACKING_ID: " + item_packing_id;
        if (cursorFeedItem.moveToFirst()) {
            sku_code = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_CODE));
            sku_name = cursorFeedItem.getString(cursorFeedItem.getColumnIndex(FeedItemEntry.COLUMN_SKU_NAME));
        }

        text += formatLine("Feed Code: " + sku_code);
        text += formatLine("Feed Name: " + sku_name);
        text += formatLine("Feed Weight: " + weight +"KG");
        text += formatLine("");
        text += formatLine("Printed by: " + sUsername);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);
        Date currentTime = Calendar.getInstance().getTime();

        text += formatLine("Date: " + sdf.format(currentTime));
        text += formatLine("Time: " + sdfTime.format(currentTime));
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("         Keluar       ") + halfLine("        Terima        "));
        text += formatLine("                  --END--                   ");


        return text;
    }
}
