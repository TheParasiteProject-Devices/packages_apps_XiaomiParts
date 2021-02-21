package org.lineageos.settings.device.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

public class PowerUtils {
    public static void setUsbFastChgStatus(boolean enabled) {
        FileUtils.writeLine(Constants.USB_FASTCHARGE_NODE, enabled ? "1" : "0");
    }

    public static boolean isUsbFastChgCurrentlyEnabled() {
        return FileUtils.getFileValueAsBoolean(Constants.USB_FASTCHARGE_NODE, false);
    }
}
