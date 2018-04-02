package my.com.engpeng.engpeng.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpeng.utilities.NetworkUtils;

/**
 * Created by Admin on 25/1/2018.
 */

public class LocationInfoAsyncTaskLoader extends AsyncTaskLoader<String> {

    private String resultJson;
    private Bundle args;

    private final LocationInfoAsyncTaskLoaderListener liatlListener;

    public interface LocationInfoAsyncTaskLoaderListener {
        void beforeLocationInfoAsyncTaskLoaderStart();
    }

    public LocationInfoAsyncTaskLoader(Context context, Bundle args, LocationInfoAsyncTaskLoaderListener liatlListener) {
        super(context);
        this.args = args;
        this.liatlListener = liatlListener;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        liatlListener.beforeLocationInfoAsyncTaskLoaderStart();

        if (resultJson != null) {
            deliverResult(resultJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return null;
        }

        String username = args.getString(AppLoader.LOADER_EXTRA_USERNAME);
        String password = args.getString(AppLoader.LOADER_EXTRA_PASSWORD);

        boolean is_local = args.getBoolean(AppLoader.LOADER_IS_LOCAL, false);

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_LOCATION_INFO, is_local);
        String result = null;
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void deliverResult(String data) {
        resultJson = data;
        super.deliverResult(data);
    }
}
