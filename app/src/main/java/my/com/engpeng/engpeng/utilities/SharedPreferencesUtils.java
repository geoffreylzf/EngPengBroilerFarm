package my.com.engpeng.engpeng.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import my.com.engpeng.engpeng.model.Bluetooth;

import static my.com.engpeng.engpeng.Global.PREF_KEY;
import static my.com.engpeng.engpeng.Global.P_KEY_BLUETOOTH_ADDRESS;
import static my.com.engpeng.engpeng.Global.P_KEY_BLUETOOTH_NAME;
import static my.com.engpeng.engpeng.Global.P_KEY_COMPANY_ID;
import static my.com.engpeng.engpeng.Global.P_KEY_LOCATION_ID;
import static my.com.engpeng.engpeng.Global.P_KEY_PASSWORD;
import static my.com.engpeng.engpeng.Global.P_KEY_PRINTER_BLUETOOTH_ADDRESS;
import static my.com.engpeng.engpeng.Global.P_KEY_PRINTER_BLUETOOTH_NAME;
import static my.com.engpeng.engpeng.Global.P_KEY_USERNAME;

/**
 * Created by Admin on 18/1/2018.
 */

public class SharedPreferencesUtils {
    public static void saveCompanyIdLocationId(Context context, int company_id, int location_id) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(P_KEY_COMPANY_ID, company_id);
        editor.putInt(P_KEY_LOCATION_ID, location_id);
        editor.apply();
    }

    public static void saveUsernamePassword(Context context, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(P_KEY_USERNAME, username);
        editor.putString(P_KEY_PASSWORD, password);
        editor.apply();
    }

    public static void saveWeighingBluetooth(Context context, Bluetooth bt) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(P_KEY_BLUETOOTH_NAME, bt.getName());
        editor.putString(P_KEY_BLUETOOTH_ADDRESS, bt.getAddress());
        editor.apply();
    }

    public static void savePrinterBluetooth(Context context, Bluetooth bt) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(P_KEY_PRINTER_BLUETOOTH_NAME, bt.getName());
        editor.putString(P_KEY_PRINTER_BLUETOOTH_ADDRESS, bt.getAddress());
        editor.apply();
    }

    public static Bluetooth getWeighingBluetooth(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return new Bluetooth(prefs.getString(P_KEY_BLUETOOTH_NAME, ""), prefs.getString(P_KEY_BLUETOOTH_ADDRESS, ""));
    }

    public static Bluetooth getPrinterBluetooth(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return new Bluetooth(prefs.getString(P_KEY_PRINTER_BLUETOOTH_NAME, ""), prefs.getString(P_KEY_PRINTER_BLUETOOTH_ADDRESS, ""));
    }

    public static void clearAll(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearCompanyIdLocationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(P_KEY_COMPANY_ID);
        editor.remove(P_KEY_LOCATION_ID);
        editor.apply();
    }

    public static void clearUsernamePassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(P_KEY_USERNAME);
        editor.remove(P_KEY_PASSWORD);
        editor.apply();
    }
}
