package com.liulishuo.filedownloader.services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadProperties;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

/**
 * Just a workaround method to use {@link android.app.job.JobService} to replace {@link android.content.Context#startForegroundService(Intent)} and
 * avoid a foreground notification showing when download tasks fired from background app
 */
public class JobServiceWorkaround {

    /**
     * Check if we should make this download task executed by a {@link android.app.job.JobService}
     * @return true if it's handled by {@link android.app.job.JobService} otherwise false
     */
    public static boolean shouldHandledByJobService() {
        if (FileDownloadHelper.getAppContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return FileDownloadUtils.needMakeServiceForeground(FileDownloadHelper.getAppContext()) &&
                    FileDownloadProperties.getImpl().useJobScheduler;
        }
        return false;
    }
}
