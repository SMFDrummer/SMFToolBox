package com.smf.toolbox;

import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.style.IOSStyle;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        DialogX.init(this);
        DialogX.globalStyle = new MaterialYouStyle() {
            public PopTipSettings popTipSettings() {
                return new PopTipSettings() {
                    @Override
                    public ALIGN align() {
                        return ALIGN.BOTTOM;
                    }
                };
            }
        };
        PopTip.build().setStyle(new IOSStyle());
        DialogX.globalTheme = DialogX.THEME.AUTO;
        DialogX.onlyOnePopTip = true;
    }
}
