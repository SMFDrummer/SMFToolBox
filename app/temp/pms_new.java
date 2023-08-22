import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class StoragePermissionChecker {

    // 定义一个接口，用于回调检查结果
    public interface OnCheckResultListener {
        void onResult(boolean result);
    }

    // 定义一个接口，用于回调获取结果
    public interface OnGetResultListener {
        void onResult(boolean result);
    }

    // 定义一个常量字符串，用于存放需要检查和获取的权限
    private static final String PERMISSION = Manifest.permission.MANAGE_EXTERNAL_STORAGE;

    // 定义一个上下文对象，用于获取PackageManager等
    private Context context;

    // 定义一个ActivityResultLauncher对象，用于启动权限请求
    private ActivityResultLauncher<Intent> requestLauncher;

    // 定义一个OnCheckResultListener对象，用于回调检查结果
    private OnCheckResultListener checkListener;

    // 定义一个OnGetResultListener对象，用于回调获取结果
    private OnGetResultListener getResultListener;

    // 构造方法，传入一个AppCompatActivity对象作为上下文，并注册ActivityResultLauncher
    public StoragePermissionChecker(AppCompatActivity activity) {
        this.context = activity;
        this.requestLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 当请求完成后，检查是否已经授予了MANAGE_EXTERNAL_STORAGE权限，并返回相应的结果
                    if (Environment.isExternalStorageManager()) {
                        if (getResultListener != null) {
                            getResultListener.onResult(true);
                        }
                    } else {
                        if (getResultListener != null) {
                            getResultListener.onResult(false);
                        }
                    }
                });
    }

    // 设置检查结果的回调监听器
    public void setOnCheckResultListener(OnCheckResultListener listener) {
        this.checkListener = listener;
    }

    // 设置获取结果的回调监听器
    public void setOnGetResultListener(OnGetResultListener listener) {
        this.getResultListener = listener;
    }

    // 检查是否已经授予了MANAGE_EXTERNAL_STORAGE权限
    public void check() {
        // 如果系统版本低于Android 13，则直接使用ContextCompat来检查权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (context.checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                if (checkListener != null) {
                    checkListener.onResult(true);
                }
            } else {
                if (checkListener != null) {
                    checkListener.onResult(false);
                }
            }
        } else {
            // 如果系统版本高于或等于Android 13，则使用Environment.isExternalStorageManager()来检查权限
            if (Environment.isExternalStorageManager()) {
                if (checkListener != null) {
                    checkListener.onResult(true);
                }
            } else {
                if (checkListener != null) {
                    checkListener.onResult(false);
                }
            }
        }
    }

    // 获取MANAGE_EXTERNAL_STORAGE权限
    public void get() {
        // 构造一个Uri对象，指定包名为当前应用的包名
        Uri uri = Uri.parse("package:" + context.getPackageName());
        // 构造一个Intent对象，指定动作为ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION，并设置数据为Uri对象
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
        // 调用ActivityResultLauncher的launch方法，传入Intent对象
        requestLauncher.launch(intent);
    }

}
