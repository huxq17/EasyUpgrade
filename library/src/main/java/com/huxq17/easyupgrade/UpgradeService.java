package com.huxq17.easyupgrade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.huxq17.download.DownloadInfo;
import com.huxq17.download.Pump;
import com.huxq17.download.message.DownloadObserver;

import java.util.HashSet;

public class UpgradeService extends Service {
    private HashSet<String> apkUrls = new HashSet<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DownloadObserver downloadObserver = new DownloadObserver() {
        @Override
        public boolean filter(DownloadInfo downloadInfo) {
            return apkUrls.contains(downloadInfo.getUrl());
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess() {
            super.onSuccess();
            DownloadInfo downloadInfo = getDownloadInfo();
            stopIfNeed(downloadInfo);
        }

        @Override
        public void onFailed() {
            super.onFailed();
            DownloadInfo downloadInfo = getDownloadInfo();
            stopIfNeed(downloadInfo);
        }
    };

    private void stopIfNeed(DownloadInfo downloadInfo) {
        String url = downloadInfo.getUrl();
        apkUrls.remove(url);
        if (downloadInfo.getProgress() == 100)
            APK.with(UpgradeService.this).from(downloadInfo.getFilePath())
                    .install();
        if (apkUrls.size() == 0) {
            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadObserver.enable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("apkUrl");
        String filePath = intent.getStringExtra("apkPath");
        apkUrls.add(url);
        Pump.newRequest(url, filePath).submit();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apkUrls.clear();
        downloadObserver.disable();
    }

    public static void start(Context context, String apkUrl, String apkPath) {
        Intent intent = new Intent(context, UpgradeService.class);
        intent.putExtra("apkUrl", apkUrl);
        intent.putExtra("apkPath", apkPath);
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, UpgradeService.class));
    }
}
