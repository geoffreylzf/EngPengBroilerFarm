package my.com.engpeng.engpeng.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpeng.utilities.NetworkUtils;

/**
 * Created by Admin on 29/1/2018.
 */

public class LogAsyncTaskLoader extends AsyncTaskLoader<String> {
    private String resultJson;
    private Bundle args;

    private final LogAsyncTaskLoaderListener latlListener;

    public interface LogAsyncTaskLoaderListener {
        void beforeLogAsyncTaskLoaderStart();
    }

    public LogAsyncTaskLoader(Context context, Bundle args, LogAsyncTaskLoaderListener latlListener) {
        super(context);
        this.args = args;
        this.latlListener = latlListener;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        latlListener.beforeLogAsyncTaskLoaderStart();

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
        String data = args.getString(AppLoader.LOADER_EXTRA_DATA);

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_SYNC_LOG, false);
        String result = null;

        try {
            result = NetworkUtils.sendPostToHttpUrl(url, username, password, data);
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
