package my.com.engpeng.engpeng.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 16/1/2018.
 */

public class EPDateUtils {
    public static String getDateDiffDesc(String msg, String date){
        Calendar calenderToday = Calendar.getInstance();
        Calendar calenderRecord = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date today;
        Date record_date;
        try {
            today = sdf.parse(sdf.format(calenderToday.getTime()));
            record_date = sdf.parse(date);

            calenderToday.setTime(today);
            calenderRecord.setTime(record_date);

            long msDiff = calenderToday.getTimeInMillis() - calenderRecord.getTimeInMillis();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

            if (daysDiff == 0) {
                msg = "Today";
            } else if (daysDiff == 1) {
                msg = "Yesterday";
            } else if (daysDiff > 1) {
                msg = "Last " + String.valueOf(daysDiff) + " days";
            } else if (daysDiff == -1) {
                msg = "Tomorrow";
            } else if (daysDiff < -1) {
                msg = "Early " + String.valueOf(daysDiff * -1) + " days";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static boolean isToday(String date){
        Calendar calenderToday = Calendar.getInstance();
        Calendar calenderRecord = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date today;
        Date record_date;

        boolean is_today = false;
        try {
            today = sdf.parse(sdf.format(calenderToday.getTime()));
            record_date = sdf.parse(date);

            calenderToday.setTime(today);
            calenderRecord.setTime(record_date);

            long msDiff = calenderToday.getTimeInMillis() - calenderRecord.getTimeInMillis();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

            if (daysDiff == 0) {
                is_today = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return is_today;
    }
}
