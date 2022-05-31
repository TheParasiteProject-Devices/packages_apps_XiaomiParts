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
import android.content.Context;
import android.os.Bundle;
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

import static org.lineageos.settings.device.Constants.KEY_KPROFILES_AUTO;
import static org.lineageos.settings.device.Constants.KPROFILES_AUTO_NODE;
import static org.lineageos.settings.device.Constants.KEY_KPROFILES_MODES;
import static org.lineageos.settings.device.Constants.KPROFILES_MODES_NODE;

public class KProfilesSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private SwitchPreference kProfilesAutoPreference;
    private ListPreference kProfilesModesPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kprofiles_settings);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        kProfilesAutoPreference = (SwitchPreference) findPreference(KEY_KPROFILES_AUTO);
        if (FileUtils.fileExists(KPROFILES_AUTO_NODE)) {
            kProfilesAutoPreference.setEnabled(true);
            kProfilesAutoPreference.setOnPreferenceChangeListener(this);
        } else {
            kProfilesAutoPreference.setSummary(R.string.kprofiles_not_supported);
            kProfilesAutoPreference.setEnabled(false);
        }
        kProfilesModesPreference = (ListPreference) findPreference(KEY_KPROFILES_MODES);
        if (FileUtils.fileExists(KPROFILES_MODES_NODE)) {
            kProfilesModesPreference.setEnabled(true);
            kProfilesModesPreference.setOnPreferenceChangeListener(this);
        } else {
            kProfilesModesPreference.setSummary(R.string.kprofiles_not_supported);
            kProfilesModesPreference.setEnabled(false);
        }
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (KEY_KPROFILES_AUTO.equals(preference.getKey())) {
            try {
                FileUtils.writeLine(KPROFILES_AUTO_NODE, (Boolean) newValue ? "Y" : "N");
            } catch(Exception e) { }

        } else if (KEY_KPROFILES_MODES.equals(preference.getKey())) {
            try {
                FileUtils.writeLine(KPROFILES_MODES_NODE, (String) newValue);
            } catch(Exception e) { }
        }
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
}
