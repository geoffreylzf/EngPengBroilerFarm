package my.com.engpeng.engpeng.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 4/1/2018.
 */

public class EngPengContract {

    public static final class BranchEntry implements BaseColumns {

        public static final String TABLE_NAME = "branch";
        public static final String COLUMN_ERP_ID = "erp_id";
        public static final String COLUMN_BRANCH_CODE = "branch_code";
        public static final String COLUMN_BRANCH_NAME = "branch_name";
        public static final String COLUMN_COMPANY_ID = "company_id";
    }

    public static final class HouseEntry implements BaseColumns {

        public static final String TABLE_NAME = "house";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";

    }

    public static final class MortalityEntry implements BaseColumns {
        public static final String TABLE_NAME = "mortality";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_M_Q = "m_q";
        public static final String COLUMN_R_Q = "r_q";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class CatchBTAEntry implements BaseColumns {
        public static final String TABLE_NAME = "catch_bta";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DOC_NUMBER = "doc_number";
        public static final String COLUMN_DOC_TYPE = "doc_type";
        public static final String COLUMN_TRUCK_CODE = "truck_code";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_PRINT_COUNT = "print_count";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class CatchBTADetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "catch_bta_detail";
        public static final String COLUMN_CATCH_BTA_ID = "weight_id"; //Don change, change will olin
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_CAGE_QTY = "cage_qty";
        public static final String COLUMN_WITH_COVER_QTY = "with_cover_qty";
    }

    public static final class TempCatchBTAEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_catch_bta";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DOC_NUMBER = "doc_number";
        public static final String COLUMN_DOC_TYPE = "doc_type";
        public static final String COLUMN_TRUCK_CODE = "truck_code";
    }

    public static final class TempCatchBTADetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_catch_bta_detail";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_CAGE_QTY = "cage_qty";
        public static final String COLUMN_WITH_COVER_QTY = "with_cover_qty";
    }

    public static final class TempWeightEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_weight";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_RECORD_TIME = "record_time";
        public static final String COLUMN_FEED = "feed";
    }

    public static final class TempWeightDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_weight_detail";
        public static final String COLUMN_SECTION = "section";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_GENDER = "gender";
    }

    public static final class WeightEntry implements BaseColumns {
        public static final String TABLE_NAME = "weight";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_RECORD_TIME = "record_time";
        public static final String COLUMN_FEED = "feed";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class WeightDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "weight_detail";
        public static final String COLUMN_WEIGHT_ID = "weight_id";
        public static final String COLUMN_SECTION = "section";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_GENDER = "gender";
    }

    public static final class StandardWeightEntry implements BaseColumns {
        public static final String TABLE_NAME = "standard_weight";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_AVG_WEIGHT = "avg_weight";
    }

    public static final class FeedItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_item";
        public static final String COLUMN_ERP_ID = "erp_id";
        public static final String COLUMN_SKU_CODE = "sku_code";
        public static final String COLUMN_SKU_NAME = "sku_name";
        public static final String COLUMN_ITEM_UOM_ID = "item_uom_id";
    }

    public static final class FeedInEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_in";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_DOC_ID = "doc_id";
        public static final String COLUMN_DOC_NUMBER = "doc_number";
        public static final String COLUMN_TRUCK_CODE = "truck_code";
        public static final String COLUMN_VARIANCE = "variance";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class FeedInDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_in_detail";
        public static final String COLUMN_FEED_IN_ID = "feed_in_id";
        public static final String COLUMN_DOC_DETAIL_ID = "doc_detail_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_COMPARTMENT_NO = "compartment_no";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class TempFeedInDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_feed_in_detail";
        public static final String COLUMN_DOC_DETAIL_ID = "doc_detail_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_COMPARTMENT_NO = "compartment_no";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class FeedTransferEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_transfer";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_RUNNING_NO = "running_no";
        public static final String COLUMN_DISCHARGE_HOUSE = "discharge_house";
        public static final String COLUMN_RECEIVE_HOUSE = "receive_house";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class FeedDischargeEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_discharge";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_DISCHARGE_CODE = "discharge_code";
        public static final String COLUMN_RUNNING_NO = "running_no";
        public static final String COLUMN_TRUCK_CODE = "truck_code";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class FeedDischargeDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_discharge_detail";
        public static final String COLUMN_FEED_DISCHARGE_ID = "feed_discharge_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class TempFeedDischargeDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_feed_discharge_detail";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class FeedReceiveEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_receive";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_RECORD_DATE = "record_date";
        public static final String COLUMN_DISCHARGE_CODE = "discharge_code";
        public static final String COLUMN_RUNNING_NO = "running_no";
        public static final String COLUMN_TRUCK_CODE = "truck_code";
        public static final String COLUMN_VARIANCE = "variance";
        public static final String COLUMN_UPLOAD = "upload";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class FeedReceiveDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_receive_detail";
        public static final String COLUMN_FEED_RECEIVE_ID = "feed_receive_id";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_WEIGHT = "weight";
    }

    public static final class TempFeedReceiveDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "temp_feed_receive_detail";
        public static final String COLUMN_HOUSE_CODE = "house_code";
        public static final String COLUMN_ITEM_PACKING_ID = "item_packing_id";
        public static final String COLUMN_WEIGHT = "weight";
    }
}
