/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.lineageos.settings.device.hbm;

import android.provider.Settings;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.DisplayUtils;
import org.lineageos.settings.device.utils.FileUtils;

public class HBMModeSwitch implements OnPreferenceChangeListener {

    private Context mContext;

    public HBMModeSwitch(Context context) {
        mContext = context;
    }

    public static String getHBM() {
        if (FileUtils.isFileWritable(Constants.HBM_NODE)) {
            return Constants.HBM_NODE;
        }
        return null;
    }

    public static String getBACKLIGHT() {
        if (FileUtils.isFileWritable(Constants.BACKLIGHT_NODE)) {
            return Constants.BACKLIGHT_NODE;
        }
        return null;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        FileUtils.writeLine(getHBM(), enabled ? "1" : "0");
        if (enabled && DisplayUtils.isAutoBrightnessEnabled(mContext.getContentResolver())) {
            FileUtils.writeLine(getBACKLIGHT(), "2047");
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        }
        return true;
    }
}
