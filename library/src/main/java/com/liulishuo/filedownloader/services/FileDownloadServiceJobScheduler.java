package com.liulishuo.filedownloader.services;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.liulishuo.filedownloader.IFileDownloadServiceProxy;
import com.liulishuo.filedownloader.model.FileDownloadHeader;
import com.liulishuo.filedownloader.util.ExtraKeys;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadLog;

public class FileDownloadServiceJobScheduler implements IFileDownloadServiceProxy {

    @Override
    public boolean start(String url, String path, boolean pathAsDirectory, int callbackProgressTimes, int callbackProgressMinIntervalMillis, int autoRetryTimes, boolean forceReDownload, FileDownloadHeader header, boolean isWifiRequired) {
        if (FileDownloadHelper.getAppContext() == null) {
            FileDownloadLog.e(this, "application context is null");
            return false;
        }

        Bundle bundle = new Bundle();
        bundle.putString(ExtraKeys.JOB_SERVICE_URL, url);
        bundle.putBoolean(ExtraKeys.JOB_SERVICE_PATH_AS_DIRECTORY, pathAsDirectory);
        bundle.putInt(ExtraKeys.JOB_SERVICE_CALLBACK_PROGRESS_TIMES, callbackProgressTimes);
        bundle.putInt(ExtraKeys.JOB_SERVICE_CALLBACK_PROGRESS_MIN_INTERVAL_MILLIS, callbackProgressMinIntervalMillis);
        bundle.putInt(ExtraKeys.JOB_SERVICE_AUTO_RETRY_TIMES, autoRetryTimes);
        bundle.putBoolean(ExtraKeys.JOB_SERVICE_FORCE_RE_DOWNLOAD, forceReDownload);
        bundle.putParcelable(ExtraKeys.JOB_SERVICE_FILE_DOWNLOAD_HEADER, header);
        bundle.putBoolean(ExtraKeys.JOB_SERVICE_IS_WIFI_REQUIRED, isWifiRequired);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final JobInfo jobInfo = new JobInfo.Builder(0,
                    new ComponentName(FileDownloadHelper.getAppContext(), FileDownloadJobService.class))
                    .setMinimumLatency(1)
                    .setOverrideDeadline(1)
                    .setTransientExtras(bundle)
                    .build();
            final JobScheduler jobScheduler = (JobScheduler) FileDownloadHelper.getAppContext()
                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.schedule(jobInfo);
            } else {
                FileDownloadLog.e(this, "Could not get job scheduler service");
            }
        }
        return false;
    }

    @Override
    public boolean pause(int id) {
        return false;
    }

    @Override
    public boolean isDownloading(String url, String path) {
        return false;
    }

    @Override
    public long getSofar(int downloadId) {
        return 0;
    }

    @Override
    public long getTotal(int downloadId) {
        return 0;
    }

    @Override
    public byte getStatus(int downloadId) {
        return 0;
    }

    @Override
    public void pauseAllTasks() {

    }

    @Override
    public boolean isIdle() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void bindStartByContext(Context context) {

    }

    @Override
    public void bindStartByContext(Context context, Runnable connectedRunnable) {

    }

    @Override
    public void unbindByContext(Context context) {

    }

    @Override
    public void startForeground(int id, Notification notification) {

    }

    @Override
    public void stopForeground(boolean removeNotification) {

    }

    @Override
    public boolean setMaxNetworkThreadCount(int count) {
        return false;
    }

    @Override
    public boolean clearTaskData(int id) {
        return false;
    }

    @Override
    public void clearAllTaskData() {

    }

    @Override
    public boolean isRunServiceForeground() {
        return false;
    }
}
