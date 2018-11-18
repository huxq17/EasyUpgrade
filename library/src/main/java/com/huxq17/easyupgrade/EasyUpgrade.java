package com.huxq17.easyupgrade;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.util.UUID;

public class EasyUpgrade {
    public static UpgradeBuilder with(Context context) {
        return new UpgradeBuilder(context);
    }

    public static class UpgradeBuilder {
        private String url;
        private String apkPath;
        private Context context;

        private UpgradeBuilder(Context context) {
            this.context = context;
        }

        public UpgradeBuilder from(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置apk下载路径，可选的
         *
         * @param apkPath
         * @return
         */
        public UpgradeBuilder into(String apkPath) {
            this.apkPath = apkPath;
            return this;
        }

        public void upgrade() {
            if (TextUtils.isEmpty(apkPath)) {
                generateApkPath();
            }
            UpgradeService.start(context, url, apkPath);
        }

        private void generateApkPath() {
            String apkName = getFileNameByUrl(url);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                apkPath = context.getExternalCacheDir().getAbsolutePath() + "/" + apkName;
            } else {
                apkPath = context.getCacheDir().getAbsolutePath() + "/" + apkName;
            }
        }

        private String getFileNameByUrl(String url) {
            int index = url.indexOf("?");
            String apkName;
            if (index != -1) {
                apkName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
            } else {
                apkName = url.substring(url.lastIndexOf("/") + 1);
            }
            if (TextUtils.isEmpty(apkName)) {
                apkName = UUID.randomUUID().toString();
            }
            return apkName;
        }
    }

    public void stop(Context context) {
        UpgradeService.stop(context);
    }
}
