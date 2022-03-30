package my.com.engpeng.engpeng.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpeng.utilities.NetworkUtils;

public class LocationAsyncTaskLoader extends AsyncTaskLoader<String> {

    private String resultJson;
    private Bundle args;

    private final LocationAsyncTaskLoader.LocationAsyncTaskLoaderListener latlListener;

    public interface LocationAsyncTaskLoaderListener {
        void beforeLocationAsyncTaskLoaderStart();
    }

    public LocationAsyncTaskLoader(Context context,
                                   Bundle args,
                                   LocationAsyncTaskLoaderListener latlListener) {
        super(context);
        this.args = args;
        this.latlListener = latlListener;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        latlListener.beforeLocationAsyncTaskLoaderStart();

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

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_LOCATION_INFO_LOCATION, is_local);
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
