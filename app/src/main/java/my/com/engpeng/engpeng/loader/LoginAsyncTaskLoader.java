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

public class LoginAsyncTaskLoader extends AsyncTaskLoader<String> {
    private String resultJson;
    private Bundle args;

    private final LoginAsyncTaskLoaderListener latlListener;

    public interface LoginAsyncTaskLoaderListener {
        void beforeLoginAsyncTaskLoaderStart();
    }

    public LoginAsyncTaskLoader(Context context, Bundle args, LoginAsyncTaskLoaderListener latlListener) {
        super(context);
        this.args = args;
        this.latlListener = latlListener;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        latlListener.beforeLoginAsyncTaskLoaderStart();

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

        boolean is_local = args.getBoolean(AppLoader.LOADER_IS_LOCAL, false);

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_AUTH_LOGIN, is_local);
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
