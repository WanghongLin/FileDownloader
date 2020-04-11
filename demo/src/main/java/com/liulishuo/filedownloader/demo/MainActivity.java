package com.liulishuo.filedownloader.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.demo.performance.PerformanceTestActivity;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

/**
 * Created by Jacksgong on 12/17/15.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // 这是只是为了全局监控。如果你有需求需要全局监控（比如用于打点/统计）可以使用这个方式，如果没有类似需求就不需要
        // 如果你有这个需求，实现FileDownloadMonitor.IMonitor接口，也使用FileDownloadMonitor.setGlobalMonitor
        // 注册进去即可
        // You do not have to add below code to your project only if you need monitor the global
        // FileDownloader Engine for statistic or others
        // If you have such requirement, just implement FileDownloadMonitor.IMonitor, and register it
        // use FileDownloadDownloader.setGlobalMonitor the same as below code.
        FileDownloadMonitor.setGlobalMonitor(GlobalMonitor.getImpl());
    }

    public void onClickMultitask(final View view) {
        startActivity(new Intent(this, MultitaskTestActivity.class));
    }

    public void onClickSingle(final View view) {
        startActivity(new Intent(this, SingleTaskTestActivity.class));
    }

    public void onClickHybridTest(final View view) {
        startActivity(new Intent(this, HybridTestActivity.class));
    }

    public void onClickTasksManager(final View view) {
        startActivity(new Intent(this, TasksManagerDemoActivity.class));
    }

    public void onClickPerformance(final View view) {
        startActivity(new Intent(this, PerformanceTestActivity.class));
    }

    public void onClickNotification(final View view){
        startActivity(new Intent(this, NotificationSampleActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_github:
                openGitHub();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openGitHub() {
        Uri uri = Uri.parse(getString(R.string.app_github_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // now our application is in background
                String path = FileDownloadUtils.getDefaultSaveFilePath(Constant.LIULISHUO_APK_URL);
                FileDownloader.getImpl().create(Constant.LIULISHUO_APK_URL)
                        .setPath(path, false)
                        .start();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unbind and stop service manually if idle
        FileDownloader.getImpl().unBindServiceIfIdle();

        FileDownloadMonitor.releaseGlobalMonitor();
    }
}
