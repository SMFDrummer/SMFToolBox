import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StoragePermissionChecker {

    // 定义一个接口，用于回调检查结果
    public interface OnCheckResultListener {
        void onResult(boolean result);
    }

    // 定义一个接口，用于回调获取结果
    public interface OnGetResultListener {
        void onResult(boolean result);
    }

    // 定义一个常量数组，用于存放需要检查和获取的权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    // 定义一个上下文对象，用于获取PackageManager等
    private Context context;

    // 定义一个ActivityResultLauncher对象，用于启动权限请求
    private ActivityResultLauncher<String[]> requestLauncher;

    // 定义一个OnCheckResultListener对象，用于回调检查结果
    private OnCheckResultListener checkListener;

    // 定义一个OnGetResultListener对象，用于回调获取结果
    private OnGetResultListener getResultListener;

    // 构造方法，传入一个AppCompatActivity对象作为上下文，并注册ActivityResultLauncher
    public StoragePermissionChecker(AppCompatActivity activity) {
        this.context = activity;
        this.requestLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    // 当请求完成后，遍历结果集合，如果有任何一个权限被拒绝，则返回false，否则返回true
                    for (boolean granted : result.values()) {
                        if (!granted) {
                            if (getResultListener != null) {
                                getResultListener.onResult(false);
                            }
                            return;
                        }
                    }
                    if (getResultListener != null) {
                        getResultListener.onResult(true);
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

    // 检查外部存储和内部存储的权限是否都已授予
    public void check() {
        // 如果系统版本低于Android 10，则直接使用ContextCompat来检查权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (checkListener != null) {
                        checkListener.onResult(false);
                    }
                    return;
                }
            }
            if (checkListener != null) {
                checkListener.onResult(true);
            }
        }
    }

    // 获取外部存储和内部存储的权限
    public void get() {
        // 调用ActivityResultLauncher的launch方法，传入需要请求的权限数组
        requestLauncher.launch(PERMISSIONS);
    }

}
