package com.liulishuo.filedownloader.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.liulishuo.filedownloader.model.FileDownloadHeader;
import com.liulishuo.filedownloader.util.ExtraKeys;
import com.liulishuo.filedownloader.util.FileDownloadLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDownloadJobService extends JobService {

    private static final String TAG = "FileDownloadJobService";
    private final Handler jobServiceHandler = new Handler();
    private static final FileDownloadManager sDownloadManager = new FileDownloadManager();
    private static final AtomicInteger sNotificationId = new AtomicInteger(0);
    private int savedNotificationId = 0;

    /**
     * A map to hold url and path, every entry of the map is a separated task
     */
    private static final Map<String, String> DOWNLOAD_URL_PATH_MAP = new HashMap<>();

    /**
     * Return a download manager for this service
     * @return a download manager
     */
    public static FileDownloadManager getDownloadManager() {
        return sDownloadManager;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.d(TAG, "onStartJob() called with: params = [" + params + "]");
        }
        jobServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                handleRunFileDownloadJob(params);

                if (!hasDownloadingTasks()) {
                    jobFinished(params, false);
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.i(TAG, "finished job");
                    }
                }
            }
        });
        return true;
    }

    /**
     * Check if the {@link FileDownloadManager} has running tasks to determine whether our job service
     * should be finished
     * @return true if downloading task current running, otherwise false, and when it's false, no side
     * effect to finish our job service
     */
    private boolean hasDownloadingTasks() {
        for (Map.Entry<String, String> urlPathEntry : DOWNLOAD_URL_PATH_MAP.entrySet()) {
            if (sDownloadManager.isDownloading(urlPathEntry.getKey(), urlPathEntry.getValue())) {
                if (FileDownloadLog.NEED_LOG) {
                    FileDownloadLog.d(TAG, "hasDownloadingTasks() called for url = " + urlPathEntry.getKey() + " and path = " + urlPathEntry.getValue());
                }
                return true;
            }
        }
        return false;
    }

    private void handleRunFileDownloadJob(JobParameters params) {
        performRunFileDownloadJob(params);
    }

    private void performRunFileDownloadJob(JobParameters params) {
        if (params != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Bundle transientExtras = params.getTransientExtras();
                if (transientExtras == null) {
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.e(TAG, "bundle data is null");
                    }
                }

                final String url = transientExtras.getString(ExtraKeys.JOB_SERVICE_URL);
                final String path = transientExtras.getString(ExtraKeys.JOB_SERVICE_PATH);
                final boolean pathAsDirectory = transientExtras.getBoolean(ExtraKeys.JOB_SERVICE_PATH_AS_DIRECTORY);
                final int callbackProgressTimes = transientExtras.getInt(ExtraKeys.JOB_SERVICE_CALLBACK_PROGRESS_TIMES);
                final int callbackProgressMinIntervalMillis = transientExtras.getInt(ExtraKeys.JOB_SERVICE_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS);
                final int autoRetryTimes = transientExtras.getInt(ExtraKeys.JOB_SERVICE_AUTO_RETRY_TIMES);
                final boolean forceReDownload = transientExtras.getBoolean(ExtraKeys.JOB_SERVICE_FORCE_RE_DOWNLOAD);
                final FileDownloadHeader downloadHeader = (FileDownloadHeader) transientExtras.getParcelable(ExtraKeys.JOB_SERVICE_FILE_DOWNLOAD_HEADER);
                final boolean isWifiRequired = transientExtras.getBoolean(ExtraKeys.JOB_SERVICE_IS_WIFI_REQUIRED);

                if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.e(TAG, "task not started with reason url = " + url + " , path = " + path);
                    }
                    return;
                }

                showNotification("FileDownloader", path.substring(path.lastIndexOf('/')+1));

                DOWNLOAD_URL_PATH_MAP.put(url, path);
                sDownloadManager.start(url, path, pathAsDirectory, callbackProgressTimes, callbackProgressMinIntervalMillis,
                        autoRetryTimes, forceReDownload, downloadHeader, isWifiRequired);
            }
        } else {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.e(this, "params is null");
            }
        }
    }

    private void showNotification(final String title, final String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("FILE_DOWNLOADER", "job_scheduler", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Job scheduler notification");
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            Notification notification = new Notification.Builder(this, notificationChannel.getId())
                    .setSmallIcon(android.R.drawable.arrow_down_float)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(PendingIntent.getActivity(this, 0,
                            new Intent().setComponent(new ComponentName("com.liulishuo.filedownloader.demo", "com.liulishuo.filedownloader.demo.MainActivity")),
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();

            savedNotificationId = sNotificationId.incrementAndGet();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(savedNotificationId, notification);
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobServiceHandler.removeCallbacksAndMessages(null);
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.d(TAG, "onStopJob() called with: params = [" + params + "]");
        }
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(savedNotificationId);
        return false;
    }
}
