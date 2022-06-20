package org.lineageos.settings.device.kprofiles;

import android.content.SharedPreferences;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

public class KProfilesUtils {

    public static void restoreKProfiles(final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean kProfilesAutoEnabled = sharedPreferences.getBoolean(Constants.KEY_KPROFILES_AUTO, false);
        FileUtils.writeLine(Constants.KPROFILES_AUTO_NODE, kProfilesAutoEnabled ? "Y" : "N");
        String kProfileMode = sharedPreferences.getString(Constants.KEY_KPROFILES_MODES, "0");
        FileUtils.writeLine(Constants.KPROFILES_MODES_NODE, kProfileMode);
    }

    public static String getCurrentKProfilesMode(final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String kProfileMode = sharedPreferences.getString(Constants.KEY_KPROFILES_MODES, "0");
        return kProfileMode;
    }
}
