package com.smf.toolbox.checker;
// 导入所需的包
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

// 定义一个存储权限检查类
public class StoragePermissionChecker {
    private static final int REQUEST_CODE = 100;

    // 定义一个返回布尔类型的check方法，该方法需要对外部存储以及内部存储权限均进行检查
    public static boolean check(Context context) {
        // 如果是 Android 11 或更高版本，需要检查 MANAGE_EXTERNAL_STORAGE 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            // 如果是 Android 8 到 Android 10，需要检查 READ_EXTERNAL_STORAGE 和 WRITE_EXTERNAL_STORAGE 权限
            int readResult = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeResult = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readResult == PackageManager.PERMISSION_GRANTED && writeResult == PackageManager.PERMISSION_GRANTED;
        }
    }

    // 定义一个合适返回类型（可能是View？）的get方法，该方法需要对外部存储以及内部存储权限均进行获取
    public static void get(Context context) {
        // 如果是 Android 11 或更高版本，需要跳转到设置界面让用户手动授予 MANAGE_EXTERNAL_STORAGE 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
                context.startActivity(intent);
                //构造Android目录的Uri
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
                //通过Uri获取DocumentFile对象
                DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
                //构造一个Intent对象，设置动作为打开文档树
                Intent intentData = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                //设置Intent的标志位，授予读写权限和持久化权限
                intentData.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                //断言DocumentFile对象不为空
                assert documentFile != null;
                //将DocumentFile对象的Uri作为初始Uri传入Intent对象
                intentData.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
                //启动Activity并等待结果
                context.startActivity(intentData);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivity(intent);
            }
        } else {
            // 如果是 Android 8 到 Android 10，需要请求 READ_EXTERNAL_STORAGE 和 WRITE_EXTERNAL_STORAGE 权限
            // 这里使用了 androidx.core.app.ActivityCompat 类来调用 requestPermissions 方法，而不是直接使用 Context 类
            // 这样可以避免在非 Activity 的上下文中出现无法解析的错误
            // 参考 [ActivityCompat.requestPermissions not working](https://stackoverflow.com/questions/32671245/activitycompat-requestpermissions-not-working)
            ActivityCompat.requestPermissions((Activity) context, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE); // 100 是请求码，可以自定义
        }
    }
    /*public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //判断请求码是否为指定的常量
        if (requestCode == REQUEST_CODE) {
            //获取返回的数据中的Uri对象
            Uri uri = data.getData();
            //判断Uri对象是否为空
            if (uri != null) {
                //通过内容解析器获取持久化Uri权限
                activity.getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
            }
        }
    }*/
    public static String getUriString(String packageName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata" + "%2F"+ packageName;
        } else {
            return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata";
        }
    }
}





