package com.smf.toolbox.checker;

import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.smf.toolbox.utils.*;

import java.util.List;

public class StoragePermissionChecker {
    protected ActivityResultLauncher<Intent> resultLauncher;


    public void setResultLauncher(ActivityResultLauncher<Intent> resultLauncher) {
        this.resultLauncher = resultLauncher;
    }

    protected boolean checkPermission(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return XXPermissions.isGranted(context, Permission.MANAGE_EXTERNAL_STORAGE) && isGranted(context,"");
        } else {
            return XXPermissions.isGranted(context, Permission.MANAGE_EXTERNAL_STORAGE) && isGranted(context, "/com.popcap.pvz2cthdbk");
        }
    }

    protected void getPermission(Activity context) {
        XXPermissions.with(context)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            startFor(context,"");
                        } else {
                            startFor(context, "/com.popcap.pvz2cthdbk");
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        MessageDialog.show("Title", "StoragePermissionCheckerError", "OK");
                    }
                });
    }

    private boolean isGranted(Activity context, String path){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            List<UriPermission> persistedUriPermissions = context.getContentResolver().getPersistedUriPermissions();
            for (UriPermission persistedUriPermission : persistedUriPermissions) {
                if (persistedUriPermission.isReadPermission() && persistedUriPermission.getUri().toString().equals("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata")) {
                    return true;
                }
            }
            return false;
        } else {
            Uri uri = Uri.parse(changeToUri(path));
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
            if (documentFile == null) return false;
            return documentFile.canWrite();
        }
    }


    //转换至uriTree的路径
    private String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String replaced = path.replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata" + replaced;
    }

    //获取指定目录的权限
    private void startFor(Activity context, String path) {
        Uri uri = Uri.parse(changeToUri(path));
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        assert documentFile != null;
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        resultLauncher.launch(intent);
    }

    public boolean check(Activity context) {
        return checkPermission(context);
    }

    public void get(Activity context) {
        getPermission(context);
    }
}





