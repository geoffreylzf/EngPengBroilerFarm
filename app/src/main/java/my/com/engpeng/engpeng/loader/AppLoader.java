package my.com.engpeng.engpeng.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by Admin on 24/1/2018.
 */

public class AppLoader implements LoaderManager.LoaderCallbacks<String> {
    public static final String LOADER_EXTRA_USERNAME = "LOADER_EXTRA_USERNAME";
    public static final String LOADER_EXTRA_PASSWORD = "LOADER_EXTRA_PASSWORD";
    public static final String LOADER_EXTRA_DATA = "LOADER_EXTRA_DATA";
    public static final String LOADER_IS_LOCAL = "LOADER_IS_LOCAL";

    private final AppLoaderListener loaderListener;

    public interface AppLoaderListener {
        Loader<String> getAsyncTaskLoader(Bundle args);

        void afterLoaderDone(String data);
    }

    public AppLoader(AppLoaderListener ulListener) {
        this.loaderListener = ulListener;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return loaderListener.getAsyncTaskLoader(args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        loaderListener.afterLoaderDone(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //just to implement, do nth
    }

}
