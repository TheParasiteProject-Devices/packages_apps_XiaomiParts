package org.lineageos.settings.device.utils;

import android.content.SharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

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

    public static void updateRefreshRateSettings(final Context context) {
        Handler.getMain().post(() -> {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String[] refreshRateSettings = sharedPreferences.getString(
            Constants.KEY_REFRESH_RATE_CONFIG, Constants.DEFAULT_REFRESH_RATE_CONFIG).split("-");

        Settings.System.putFloat(context.getContentResolver(),
            Settings.System.MIN_REFRESH_RATE, Float.valueOf(refreshRateSettings[0]));
        Settings.System.putFloat(context.getContentResolver(),
            Settings.System.PEAK_REFRESH_RATE, Float.valueOf(refreshRateSettings[1]));
        });
    }
}
