/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2018-2021 crDroid Android Project
* Copyright (C) 2019-2022 Evolution X Project
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.TwoStatePreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.DisplayUtils;
import org.lineageos.settings.device.R;

public class HBMFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = HBMFragment.class.getSimpleName();

    private static TwoStatePreference mHBMModeSwitch;
    private static TwoStatePreference mAutoHBMSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        addPreferencesFromResource(R.xml.hbm_settings);

        // HBM
        mHBMModeSwitch = (TwoStatePreference) findPreference(Constants.KEY_HBM_SWITCH);
        mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch(getContext()));

        // AutoHBM
        mAutoHBMSwitch = (TwoStatePreference) findPreference(Constants.KEY_AUTO_HBM_SWITCH);
        mAutoHBMSwitch.setOnPreferenceChangeListener(this);
        mAutoHBMSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(Constants.KEY_AUTO_HBM_SWITCH, false));
    }

    public static boolean isAUTOHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.KEY_AUTO_HBM_SWITCH, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHBMModeSwitch.setChecked(DisplayUtils.isHBMCurrentlyEnabled());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHBMModeSwitch)  {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(Constants.KEY_HBM_SWITCH, enabled).commit();
            DisplayUtils.enableHBMService(getContext());
            return true;
        } else if (preference == mAutoHBMSwitch) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(Constants.KEY_AUTO_HBM_SWITCH, enabled).commit();
            DisplayUtils.enableHBMService(getContext());
            return true;
        }

        return false;
    }
}
