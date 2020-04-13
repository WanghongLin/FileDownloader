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

    private static final String TAG = "FileDownloadServiceJobS";

    @Override
    public boolean start(String url, String path, boolean pathAsDirectory, int callbackProgressTimes, int callbackProgressMinIntervalMillis, int autoRetryTimes, boolean forceReDownload, FileDownloadHeader header, boolean isWifiRequired) {
        if (FileDownloadHelper.getAppContext() == null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.e(TAG, "application context is null");
            }
            return false;
        }

        final Bundle bundle = new Bundle();
        bundle.putString(ExtraKeys.JOB_SERVICE_URL, url);
        bundle.putString(ExtraKeys.JOB_SERVICE_PATH, path);
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
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .build();
            final JobScheduler jobScheduler = (JobScheduler) FileDownloadHelper.getAppContext()
                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.schedule(jobInfo);
            } else {
                if (FileDownloadLog.NEED_LOG) {
                    FileDownloadLog.e(TAG, "Could not get job scheduler service");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean pause(int id) {
        return FileDownloadJobService.getDownloadManager().pause(id);
    }

    @Override
    public boolean isDownloading(String url, String path) {
        return FileDownloadJobService.getDownloadManager().isDownloading(url, path);
    }

    @Override
    public long getSofar(int downloadId) {
        return FileDownloadJobService.getDownloadManager().getSoFar(downloadId);
    }

    @Override
    public long getTotal(int downloadId) {
        return FileDownloadJobService.getDownloadManager().getTotal(downloadId);
    }

    @Override
    public byte getStatus(int downloadId) {
        return FileDownloadJobService.getDownloadManager().getStatus(downloadId);
    }

    @Override
    public void pauseAllTasks() {
        FileDownloadJobService.getDownloadManager().pauseAll();
    }

    @Override
    public boolean isIdle() {
        return FileDownloadJobService.getDownloadManager().isIdle();
    }

    @Override
    public boolean isConnected() {
        // always return true
        return true;
    }

    @Override
    public void bindStartByContext(Context context) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.w(TAG, "do nothing for bindStartByContext() called with: context = [" + context + "]");
        }
    }

    @Override
    public void bindStartByContext(Context context, Runnable connectedRunnable) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.w(TAG, "do nothing for bindStartByContext() called with: context = [" + context + "], connectedRunnable = [" + connectedRunnable + "]");
        }
    }

    @Override
    public void unbindByContext(Context context) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.w(TAG, "do nothing for unbindByContext() called with: context = [" + context + "]");
        }
    }

    @Override
    public void startForeground(int id, Notification notification) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.w(TAG, "do nothing for startForeground() called with: id = [" + id + "], notification = [" + notification + "]");
        }
    }

    @Override
    public void stopForeground(boolean removeNotification) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.w(TAG, "do nothing for stopForeground() called with: removeNotification = [" + removeNotification + "]");
        }
    }

    @Override
    public boolean setMaxNetworkThreadCount(int count) {
        return FileDownloadJobService.getDownloadManager().setMaxNetworkThreadCount(count);
    }

    @Override
    public boolean clearTaskData(int id) {
        return FileDownloadJobService.getDownloadManager().clearTaskData(id);
    }

    @Override
    public void clearAllTaskData() {
        FileDownloadJobService.getDownloadManager().clearAllTaskData();
    }

    @Override
    public boolean isRunServiceForeground() {
        return false;
    }
}
