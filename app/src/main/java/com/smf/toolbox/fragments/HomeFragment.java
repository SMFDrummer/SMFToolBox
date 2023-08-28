package com.smf.toolbox.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.kongzue.dialogx.dialogs.PopTip;
import com.smf.toolbox.R;
import com.smf.toolbox.checker.StoragePermissionChecker;

public class HomeFragment extends Fragment {
    /**
     * Fragment的生命周期主要包括以下几个阶段：
     * onAttach(): 当Fragment与Activity建立关联时调用。
     * onCreate(): 创建Fragment时调用。
     * onCreateView(): 创建与Fragment关联的视图时调用。
     * onActivityCreated(): 当与Fragment关联的Activity完成onCreate()方法时调用。
     * onAttachFragment(): 当在当前Fragment中添加另一个Fragment时调用。
     * onStart(): 当Fragment对用户可见时调用。
     * onResume(): 当Fragment处于活动状态时调用。
     * onPause(): 当Fragment不再活动时调用。
     * onStop(): 当Fragment不再可见时调用。
     * onDestroyView(): 当与Fragment关联的视图被移除时调用。
     * onDestroy(): 销毁Fragment时调用。
     * onDetach(): 当Fragment与Activity解除关联时调用。
     * 以上就是Fragment的生命周期，每个阶段都有对应的方法，可以根据需要在这些方法中实现相应的功能。
     */
    View view;
    MaterialCardView configChecker;
    ShapeableImageView checkerImage;
    MaterialTextView checkerText;
    private StoragePermissionChecker storagePermissionChecker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storagePermissionChecker = new StoragePermissionChecker();
        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri;
                            if (data != null && (uri = data.getData()) != null) {
                                requireContext().getContentResolver().takePersistableUriPermission(
                                        uri, data.getFlags() & (
                                          Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        )
                                );
                                PopTip.show("OK");
                            }
                        }
                    }
                });
        storagePermissionChecker.setResultLauncher(resultLauncher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        configChecker = view.findViewById(R.id.configChecker);
        checkerImage = view.findViewById(R.id.checkerImage);
        checkerText = view.findViewById(R.id.checkerText);

        configChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!storagePermissionChecker.check(requireActivity())) {
                    storagePermissionChecker.get(requireActivity());
                }
            }
        });

        configChecker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DocumentFile documentFile = DocumentFile.fromTreeUri(requireContext(), Uri.parse(changeToUri("/com.popcap.pvz2cthdbk")));
                if (documentFile != null) {
                    if (documentFile.exists()) PopTip.show("true");
                    else PopTip.show("!null & false");
                } else PopTip.show("false");
                return false;
            }
        });

        return view;
    }

    //转换至uriTree的路径
    private String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String replaced = path.replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata" + replaced;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (storagePermissionChecker.check(requireActivity())) {
            checker_checked(configChecker, checkerImage, checkerText);
        } else {
            checker_uncheck(configChecker, checkerImage, checkerText);
            PopTip.show("未获取权限");
        }
    }


    private void checker_checked(MaterialCardView configChecker, ShapeableImageView checkerImage, MaterialTextView checkerText) {
        configChecker.setCardBackgroundColor(MaterialColors.getColor(checkerImage, R.attr.colorSuccessContainer));

        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_checked);
        checkerImage.setImageDrawable(drawable);
        checkerImage.setImageTintList(ColorStateList.valueOf(MaterialColors.getColor(checkerImage, R.attr.colorSuccess)));

        checkerText.setText(R.string.checked);
        checkerText.setTextColor(MaterialColors.getColor(checkerImage, R.attr.colorOnSuccessContainer));
    }

    private void checker_uncheck(MaterialCardView configChecker, ShapeableImageView checkerImage, MaterialTextView checkerText) {
        configChecker.setCardBackgroundColor(MaterialColors.getColor(checkerImage, R.attr.colorErrorContainer));

        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_uncheck);
        checkerImage.setImageDrawable(drawable);
        checkerImage.setImageTintList(ColorStateList.valueOf(MaterialColors.getColor(checkerImage, R.attr.colorError)));

        checkerText.setText(R.string.uncheck);
        checkerText.setTextColor(MaterialColors.getColor(checkerImage, R.attr.colorOnErrorContainer));
    }

}