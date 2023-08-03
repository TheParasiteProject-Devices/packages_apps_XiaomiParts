package org.lineageos.settings.device.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.os.UserHandle;
import android.provider.Settings;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.hbm.AutoHBMService;
import org.lineageos.settings.device.hbm.HBMFragment;
import org.lineageos.settings.device.utils.FileUtils;

public class DisplayUtils {
    public static void setDcDimmingStatus(boolean enabled) {
        FileUtils.writeLine(Constants.DC_DIMMING_NODE, enabled ? "1" : "0");
    }

    private static boolean mHBMServiceEnabled = false;

    private static void startHBMService(Context context) {
        context.startServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
        mHBMServiceEnabled = true;
    }

    private static void stopHBMService(Context context) {
        mHBMServiceEnabled = false;
        context.stopServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
    }

    public static void enableHBMService(Context context) {
        if (HBMFragment.isAUTOHBMEnabled(context) && !mHBMServiceEnabled) {
            startHBMService(context);
        } else if (!HBMFragment.isAUTOHBMEnabled(context) && mHBMServiceEnabled) {
            stopHBMService(context);
        }
    }

    public static boolean isHBMCurrentlyEnabled() {
        return FileUtils.getFileValueAsBoolean(Constants.HBM_NODE, false);
    }

    public static boolean isAutoBrightnessEnabled(ContentResolver contentResolver) {
        return Settings.System.getInt(contentResolver, 
                    SCREEN_BRIGHTNESS_MODE , SCREEN_BRIGHTNESS_MODE_MANUAL) 
                        == SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? true : false;
    }
}
