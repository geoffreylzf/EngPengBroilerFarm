package my.com.engpeng.engpeng.jobService;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import my.com.engpeng.engpeng.utilities.NotificationUtils;

/**
 * Created by Admin on 12/2/2018.
 */

public class ReminderFirebaseJobService extends JobService {

    private AsyncTask backgroundAT;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        backgroundAT = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                Context context = ReminderFirebaseJobService.this;
                NotificationUtils.remindUserBecauseCharging(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };
        backgroundAT.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (backgroundAT != null) backgroundAT.cancel(true);
        return true;
    }
}
