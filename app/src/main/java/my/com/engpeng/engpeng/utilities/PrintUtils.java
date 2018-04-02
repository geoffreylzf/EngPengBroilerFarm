package my.com.engpeng.engpeng.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import my.com.engpeng.engpeng.controller.BranchController;
import my.com.engpeng.engpeng.controller.CatchBTAController;
import my.com.engpeng.engpeng.controller.CatchBTADetailController;
import my.com.engpeng.engpeng.data.EngPengContract;

import static my.com.engpeng.engpeng.Global.sUsername;

/**
 * Created by Admin on 28/2/2018.
 */

public class PrintUtils {

    private static final String PRINT_END = "\n\n\n\n\n";
    private static final String PRINT_SEPERATOR = "---------------------------------------------";
    private static final String PRINT_HALF_SEPERATOR = "----------------------";

    private static final double WEIGHT_COVER_WITH_CAGE = 8;
    private static final double WEIGHT_COVER_WITHOUT_CAGE = 7.6;

    public static String printCatchBTA(SQLiteDatabase db, long catch_bta_id) {

        String text = "";

        Cursor cursorCatchBTA = CatchBTAController.getById(db, catch_bta_id);
        cursorCatchBTA.moveToFirst();
        String record_date = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_RECORD_DATE));
        String doc_number = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_DOC_NUMBER));
        String doc_type = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_DOC_TYPE));
        String type = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_TYPE));
        if (type.equals("B")) {
            type = "C";
        }

        String truck_code = cursorCatchBTA.getString(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_TRUCK_CODE));
        int company_id = cursorCatchBTA.getInt(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_COMPANY_ID));
        int location_id = cursorCatchBTA.getInt(cursorCatchBTA.getColumnIndex(EngPengContract.CatchBTAEntry.COLUMN_LOCATION_ID));

        Cursor cursorCompany = BranchController.getBranchByErpId(db, company_id);
        cursorCompany.moveToFirst();
        String company_code = cursorCompany.getString(cursorCompany.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
        String company_name = cursorCompany.getString(cursorCompany.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

        Cursor cursorLocation = BranchController.getBranchByErpId(db, location_id);
        cursorLocation.moveToFirst();
        String location_code = cursorLocation.getString(cursorLocation.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_CODE));
        String location_name = cursorLocation.getString(cursorLocation.getColumnIndex(EngPengContract.BranchEntry.COLUMN_BRANCH_NAME));

        text += formatLine("");
        text += formatLine(company_name);
        text += formatLine("Bill Timbangan Ayam");
        text += formatLine("Date: " + record_date);
        text += formatLine("PL # / IFT # / OP #: " + doc_number);
        text += formatLine("Truck Code : " + truck_code);
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

            for (int x = 0; x < firstColumnRow; x++) {
                cursorCatchBTADetail.moveToPosition(x);
                String num = formatNumber(3, x + 1);
                double weight = cursorCatchBTADetail.getDouble(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_WEIGHT));
                String qty = cursorCatchBTADetail.getString(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_QTY));
                int cage_qty = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_CAGE_QTY));

                int with_cover_qty = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
                int without_cover_qty = cage_qty - with_cover_qty;

                ttl_qty += Integer.parseInt(qty);
                ttl_wgt += weight;


                ttl_cage += cage_qty;
                ttl_cage_with_cover += with_cover_qty;
                ttl_wgt_cage_with_cover += with_cover_qty * WEIGHT_COVER_WITH_CAGE;
                ttl_cage_without_cover += without_cover_qty;
                ttl_wgt_cage_without_cover += without_cover_qty * WEIGHT_COVER_WITHOUT_CAGE;


                String leftColumn = btaTableRow(num, weight, qty);

                String rightColumn = "";

                if (x + firstColumnRow < row) {
                    cursorCatchBTADetail.moveToPosition(x + firstColumnRow);
                    String num2 = formatNumber(3, x + 1 + firstColumnRow);
                    double weight2 = cursorCatchBTADetail.getDouble(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_WEIGHT));
                    String qty2 = cursorCatchBTADetail.getString(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_QTY));

                    int cage_qty2 = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_CAGE_QTY));
                    int with_cover_qty2 = cursorCatchBTADetail.getInt(cursorCatchBTADetail.getColumnIndex(EngPengContract.CatchBTADetailEntry.COLUMN_WITH_COVER_QTY));
                    int without_cover_qty2 = cage_qty2 - with_cover_qty2;

                    ttl_qty += Integer.parseInt(qty2);
                    ttl_wgt += weight2;

                    ttl_cage += cage_qty2;
                    ttl_cage_with_cover += with_cover_qty2;
                    ttl_wgt_cage_with_cover += with_cover_qty2 * WEIGHT_COVER_WITH_CAGE;
                    ttl_cage_without_cover += without_cover_qty2;
                    ttl_wgt_cage_without_cover += without_cover_qty2 * WEIGHT_COVER_WITHOUT_CAGE;

                    rightColumn = btaTableRow(num2, weight2, qty2);
                }


                text += formatLine(leftColumn + "|" + rightColumn);
            }

            text += formatLine(halfLine("") + "|" + halfLine(""));
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
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine("-");
        text += formatLine(halfLine("    --------------    ") + halfLine("    --------------    "));
        text += formatLine(halfLine("   Mandor/Supervisor  ") + halfLine("        Driver        "));
        text += formatLine("                  --END--                   ");

        text += PRINT_END;
        return text;
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

    private static String btaTableHeader() {
        return "  #     Weight  Qty   ";
    }

    private static String btaTableRow(String num, double weight, String qty) {
        return String.format(" %3s  %8.02f  %3s   ", num, weight, qty);
    }
}
