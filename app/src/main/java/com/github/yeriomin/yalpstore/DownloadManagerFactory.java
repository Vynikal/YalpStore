package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class DownloadManagerFactory {

    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    static public DownloadManagerInterface get(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
            || !nativeDownloadManagerEnabled(context)
            || nougatVpn(context)
        ) {
            Log.i(DownloadManagerFactory.class.getSimpleName(), "DownloadManager unavailable - using a fallback");
            return new DownloadManagerFake(context);
        } else {
            Log.i(DownloadManagerFactory.class.getSimpleName(), "DownloadManager is found and is going to be used");
            return new DownloadManagerAdapter(context);
        }
    }

    static private boolean nativeDownloadManagerEnabled(Context context) {
        int state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        try {
            state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
        } catch (Throwable e) {
            Log.w(DownloadManagerFactory.class.getSimpleName(), "Could not check DownloadManager status: " + e.getMessage());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
            );
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            );
        }
    }

    static private boolean nougatVpn(Context context) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N && Build.VERSION.SDK_INT != Build.VERSION_CODES.N_MR1) {
            return false;
        }
        return NetworkState.isVpn(context);
    }
}
