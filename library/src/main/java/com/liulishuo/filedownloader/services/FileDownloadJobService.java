package com.liulishuo.filedownloader.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;

public class FileDownloadJobService extends JobService {
    private final Handler jobServiceHandler = new Handler();
    private final FileDownloadManager downloadManager = new FileDownloadManager();

    @Override
    public boolean onStartJob(final JobParameters params) {
        jobServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                handleRunFileDownloadJob(params);
                jobFinished(params, false);
            }
        });
        return true;
    }

    private void handleRunFileDownloadJob(JobParameters params) {
        performRunFileDownloadJob(params);
    }

    private void performRunFileDownloadJob(JobParameters params) {

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobServiceHandler.removeCallbacksAndMessages(null);
        return false;
    }
}
