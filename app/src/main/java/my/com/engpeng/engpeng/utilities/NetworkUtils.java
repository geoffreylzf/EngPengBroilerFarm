package my.com.engpeng.engpeng.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by Admin on 16/1/2018.
 */

public class NetworkUtils {

    final static String DEVELOPMENT_URL_TAKE_NOTE_DONT_USE_THIS_WHEN_UPLOAD_TO_STORE = "http://192.168.9.28:8833/eperp/";

    final static String DEPLOYMENT_URL = "http://epgroup.dlinkddns.com:5035/eperp/";
    final static String ENGPENG_BASE_URL = DEPLOYMENT_URL;
    final static String LOCAL_URL = "http://192.168.8.1:8833/eperp/";
    final static String PARAM_MODULE = "r";

    final public static String MODULE_AUTH_LOGIN = "MobileAuth/Login";
    final public static String MODULE_LOCATION_INFO = "MobileLocationInfo/List";
    final public static String MODULE_SYNC_UPLOAD = "MobileSync/Upload";
    final public static String MODULE_SYNC_LOG = "MobileSync/Log";


    final public static String ENCODE = "UTF-8";

    public static URL buildUrl(String module, boolean is_local) {
        String connection_url = ENGPENG_BASE_URL;

        if(is_local){
            connection_url = LOCAL_URL;
        }

        Uri builtUri = Uri.parse(connection_url).buildUpon()
                .appendQueryParameter(PARAM_MODULE, module)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url, String username, String password) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String user_pass = username + ":" + password;
        String basic_auth = "Basic " + new String(Base64.encode(user_pass.getBytes(), android.util.Base64.DEFAULT));
        urlConnection.setRequestProperty ("Authorization", basic_auth);

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String sendPostToHttpUrl(URL url, String username, String password, String data) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String user_pass = username + ":" + password;
        String basic_auth = "Basic " + new String(Base64.encode(user_pass.getBytes(), android.util.Base64.DEFAULT));
        urlConnection.setRequestProperty ("Authorization", basic_auth);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);

        try {
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
