package my.com.engpeng.engpeng.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class PrintPreviewAsyncTaskLoader extends AsyncTaskLoader<String> {

    private final PrintPreviewAsyncTaskLoaderListener ppatlListener;
    private String is_complete = "false";

    public interface PrintPreviewAsyncTaskLoaderListener {
        void beforePrintPreviewAsyncTaskLoaderStart();
        String afterPrintPreviewAsyncTaskLoaderStart();
    }

    public PrintPreviewAsyncTaskLoader(Context context, PrintPreviewAsyncTaskLoaderListener ppatlListener) {
        super(context);
        this.ppatlListener = ppatlListener;
    }

    @Override
    protected void onStartLoading() {
        ppatlListener.beforePrintPreviewAsyncTaskLoaderStart();

        if (is_complete.equals("true")) {
            deliverResult(is_complete);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        return ppatlListener.afterPrintPreviewAsyncTaskLoaderStart();
    }

    @Override
    public void deliverResult(String is_complete) {
        this.is_complete = is_complete;
        super.deliverResult(is_complete);
    }
}
