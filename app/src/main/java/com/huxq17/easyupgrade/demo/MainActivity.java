package com.huxq17.easyupgrade.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.huxq17.download.DownloadInfo;
import com.huxq17.download.Pump;
import com.huxq17.download.listener.DownloadObserver;
import com.huxq17.easyupgrade.EasyUpgrade;

public class MainActivity extends AppCompatActivity {
    private String apkUrl = "http://v.nq6.com/xinqu.apk";
    private ProgressDialog progressDialog;
    DownloadObserver observer = new DownloadObserver() {
        @Override
        public void onProgress(int progress) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            progressDialog.setProgress(progress);
        }

        @Override
        public boolean filter(DownloadInfo downloadInfo) {
            return downloadInfo.getUrl().equals(apkUrl);
        }

        @Override
        public void onSuccess() {
            super.onSuccess();
            progressDialog.dismiss();
        }

        @Override
        public void onFailed() {
            super.onFailed();
            progressDialog.dismiss();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pump.subscribe(observer);
        initProgressDialog();
    }

    public void onClick(View v) {
        EasyUpgrade.with(this).from(apkUrl).upgrade();
//        //如果想自定义下载路径，可以使用into
//        EasyUpgrade.with(this)
//                .from(apkUrl).into()
//                .upgrade();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading");
//        progressDialog.setMessage("Downloading now...");
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Pump.unSubscribe(observer);
        if (progressDialog != null) progressDialog.dismiss();
    }
}
