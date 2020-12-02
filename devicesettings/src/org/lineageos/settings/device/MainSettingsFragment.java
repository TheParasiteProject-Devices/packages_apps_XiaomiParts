/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.DisplayUtils;

public class MainSettingsFragment extends PreferenceFragment {

    private Preference mPrefRefreshRateInfo;
    private ListPreference mPrefRefreshRateConfig;
    private SwitchPreference mPrefDcDimming;
    private SwitchPreference mPrefHBM;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_settings);
        mPrefRefreshRateConfig = (ListPreference) findPreference(Constants.KEY_REFRESH_RATE_CONFIG);
        mPrefRefreshRateConfig.setOnPreferenceChangeListener(PrefListener);
        mPrefRefreshRateInfo = (Preference) findPreference(Constants.KEY_REFRESH_RATE_INFO);
        mPrefDcDimming = (SwitchPreference) findPreference(Constants.KEY_DC_DIMMING);
        mPrefDcDimming.setOnPreferenceChangeListener(PrefListener);
        mPrefHBM = (SwitchPreference) findPreference(Constants.KEY_HBM);
        mPrefHBM.setOnPreferenceChangeListener(PrefListener);
        updateSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPrefHBM.setChecked(DisplayUtils.isHBMEnabled(getContext()));
    }

    private Preference.OnPreferenceChangeListener PrefListener =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                final String key = preference.getKey();

                if (Constants.KEY_REFRESH_RATE_CONFIG.equals(key)) {
                    setHzConfig();
                } else if (Constants.KEY_DC_DIMMING.equals(key)) {
                    DisplayUtils.setDcDimmingStatus((boolean) value);
                } else if (Constants.KEY_HBM.equals(key)) {
                    DisplayUtils.setHBMStatus((boolean) value);
                    Intent mHbmIntent = new Intent(getContext(), HbmService.class);
                    if (DisplayUtils.isHBMEnabled(getContext()) == true) {
                        getContext().startService(mHbmIntent);
                    } else {
                        getContext().stopService(mHbmIntent);
                        mHbmIntent = null;
                    }
                }

                return true;
            }
        };

    private float getCurrentMinHz() {
        return Settings.System.getFloat(getContext().getContentResolver(),
            Settings.System.MIN_REFRESH_RATE, Constants.DEFAULT_REFRESH_RATE);
    }

    private float getCurrentMaxHz() {
        return Settings.System.getFloat(getContext().getContentResolver(),
            Settings.System.PEAK_REFRESH_RATE, Constants.DEFAULT_REFRESH_RATE);
    }

    private void setHzConfig() {
        DisplayUtils.updateRefreshRateSettings(getContext());
        updateSummary();
    }

    private void updateSummary() {
        if (mPrefRefreshRateConfig.getEntry() == null) {
            mPrefRefreshRateConfig.setValueIndex(2);
        }
        Handler.getMain().post(() -> {
            mPrefRefreshRateInfo.setSummary(
                String.format(getString(R.string.current_refresh_rate_info),
                    String.valueOf(Math.round(getCurrentMaxHz())), String.valueOf(Math.round(getCurrentMinHz()))));
        });
    }
}
