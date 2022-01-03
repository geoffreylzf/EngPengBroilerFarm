package my.com.engpeng.engpeng.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

import my.com.engpeng.engpeng.utilities.NetworkUtils;

public class PersonStaffAsyncTaskLoader extends AsyncTaskLoader<String> {
    private String resultJson;
    private Bundle args;

    private final PersonStaffAsyncTaskLoaderListener psatlListener;

    public interface PersonStaffAsyncTaskLoaderListener {
        void beforeLocationInfoAsyncTaskLoaderStart();
    }

    public PersonStaffAsyncTaskLoader(Context context, Bundle args, PersonStaffAsyncTaskLoader.PersonStaffAsyncTaskLoaderListener psatlListener) {
        super(context);
        this.args = args;
        this.psatlListener = psatlListener;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return;
        }

        psatlListener.beforeLocationInfoAsyncTaskLoaderStart();

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

        URL url = NetworkUtils.buildUrl(NetworkUtils.MODULE_LOCATION_INFO_PERSON_STAFF, is_local);
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
