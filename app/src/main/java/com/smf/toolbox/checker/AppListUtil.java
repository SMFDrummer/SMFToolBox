package com.smf.toolbox.checker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.dialogs.MessageDialog;

import java.util.List;

public class AppListUtil {
    private static boolean checkPermission(Context context) {
        return XXPermissions.isGranted(context, Permission.GET_INSTALLED_APPS);
    }

    private static void getPermission(Context context) {
        XXPermissions.with(context)
                .permission(Permission.GET_INSTALLED_APPS)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        getAppList(context);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        MessageDialog.show("Title", "AppListUtilError", "OK");
                    }
                });
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static void getAppList(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            int flags = PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES;
            List<PackageInfo> packageInfoList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfoList = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags));
            } else {
                packageInfoList = packageManager.getInstalledPackages(flags);
            }

            for (PackageInfo info : packageInfoList) {
                Log.i("XXPermissions", "应用包名：" + info.packageName);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static boolean check(Context context) {
        return checkPermission(context);
    }

    public static void get(Context context) {
        getPermission(context);
    }
}
