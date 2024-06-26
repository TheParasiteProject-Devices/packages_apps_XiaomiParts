/*
 * Copyright (C) 2022 CannedShroud
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

package org.lineageos.settings.device.kprofiles;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;

import org.lineageos.settings.device.Constants;

public class KProfilesSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private SwitchPreference kProfilesAutoPreference;
    private ListPreference kProfilesModesPreference;
    private Preference kProfilesModesInfo;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_KPROFILE_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                final String value = FileUtils.readOneLine(Constants.KPROFILES_MODES_NODE);
                kProfilesModesPreference.setValue(value != null ? value : "0");
                updateTitle();
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kprofiles_settings);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        kProfilesAutoPreference = (SwitchPreference) findPreference(Constants.KEY_KPROFILES_AUTO);
        if (FileUtils.fileExists(Constants.KPROFILES_AUTO_NODE)) {
            kProfilesAutoPreference.setEnabled(true);
            kProfilesAutoPreference.setOnPreferenceChangeListener(this);
        } else {
            kProfilesAutoPreference.setSummary(R.string.kprofiles_not_supported);
            kProfilesAutoPreference.setEnabled(false);
        }
        kProfilesModesPreference = (ListPreference) findPreference(Constants.KEY_KPROFILES_MODES);
        if (FileUtils.fileExists(Constants.KPROFILES_MODES_NODE)) {
            kProfilesModesPreference.setEnabled(true);
            kProfilesModesPreference.setOnPreferenceChangeListener(this);
            updateTitle();
        } else {
            kProfilesModesPreference.setSummary(R.string.kprofiles_not_supported);
            kProfilesModesPreference.setEnabled(false);
        }
        kProfilesModesInfo = (Preference) findPreference(Constants.KPROFILES_MODES_INFO);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_KPROFILE_SETTING_CHANGED);
        getContext().registerReceiver(stateReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.kprofiles,
                container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(stateReceiver);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (Constants.KEY_KPROFILES_AUTO.equals(preference.getKey())) {
            try {
                FileUtils.writeLine(Constants.KPROFILES_AUTO_NODE, (Boolean) newValue ? "Y" : "N");
            } catch(Exception e) { }

        } else if (Constants.KEY_KPROFILES_MODES.equals(preference.getKey())) {
            try {
                FileUtils.writeLine(Constants.KPROFILES_MODES_NODE, (String) newValue);
                updateTitle();
            } catch(Exception e) { }
        }

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_KPROFILE_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    private String modesDesc() {
        String mode = KProfilesUtils.getCurrentKProfilesMode(getContext());
        String descrpition = null;
        switch (mode) {
            case "0":
                descrpition = getString(R.string.kprofiles_modes_none_description);
                break;
            case "1":
                descrpition = getString(R.string.kprofiles_modes_battery_description);
                break;
            case "2":
                descrpition = getString(R.string.kprofiles_modes_balanced_description);
                break;
            case "3":
                descrpition = getString(R.string.kprofiles_modes_performance_description);
                break;
        }
        return descrpition;
    }

    private void updateTitle() {
        Handler.getMain().post(() -> {
            kProfilesModesInfo.setTitle(
                String.format(getString(R.string.kprofiles_modes_description),
                    modesDesc()));
        });
    }
}
