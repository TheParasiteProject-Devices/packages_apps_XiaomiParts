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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.TextView;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.DisplayUtils;
import org.lineageos.settings.device.utils.PowerUtils;

public class MainSettingsFragment extends PreferenceFragment {

    private Preference mPrefRefreshRateInfo;
    private ListPreference mPrefRefreshRateConfig;
    private SwitchPreference mPrefDcDimming;
    private SwitchPreference mPrefUsbFastChg;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_REFRESH_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                mPrefRefreshRateConfig.setValue(Math.round(getCurrentMaxHz())+"-"+Math.round(getCurrentMinHz()));
                updateSummary();
            } else if (intent.getAction().equals(Constants.ACTION_DCDIMMING_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                mPrefDcDimming.setChecked(intent.getBooleanExtra(Constants.DCDIMMING_STATE, false));
            }
        }
    };

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
        mPrefUsbFastChg = (SwitchPreference) findPreference(Constants.KEY_USB_FASTCHARGE);
        mPrefUsbFastChg.setOnPreferenceChangeListener(PrefListener);
        updateSummary();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_REFRESH_SETTING_CHANGED);
        filter.addAction(Constants.ACTION_DCDIMMING_SETTING_CHANGED);
        getContext().registerReceiver(stateReceiver, filter);
    }

    private Preference.OnPreferenceChangeListener PrefListener =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                final String key = preference.getKey();

                if (Constants.KEY_REFRESH_RATE_CONFIG.equals(key)) {
                    setHzConfig();
                    mSelfChange = true;
                    final Intent intent = new Intent(Constants.ACTION_REFRESH_SETTING_CHANGED);
                    intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                    getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
                } else if (Constants.KEY_DC_DIMMING.equals(key)) {
                    DisplayUtils.setDcDimmingStatus((boolean) value);
                    mSelfChange = true;
                    final Intent intent = new Intent(Constants.ACTION_DCDIMMING_SETTING_CHANGED);
                    intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                    intent.putExtra(Constants.DCDIMMING_STATE, (boolean) value);
                    getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
                } else if (Constants.KEY_USB_FASTCHARGE.equals(key)) {
                    PowerUtils.setUsbFastChgStatus((boolean) value);
                }

                return true;
            }
        };


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(stateReceiver);
    }

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
