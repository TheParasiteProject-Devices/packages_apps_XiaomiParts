/*
 * Copyright (C) 2020 ArrowOS
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

package org.lineageos.settings.device.color;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;
import org.lineageos.settings.device.utils.KcalUtils;

public class KcalSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnCheckedChangeListener {

    private static final String TAG = "KcalSettings";

    private SharedPreferences mSharedPrefs;

    private MainSwitchPreference mKcalSwitchPreference;
    private Preference mResetButton;
    private Preference mColorProfilesButton;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kcal_settings);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mKcalSwitchPreference = (MainSwitchPreference) findPreference(Constants.KEY_KCAL_ENABLE);
        mResetButton = (Preference) findPreference(Constants.KEY_KCAL_RESET_DEFAULT);

        // Check if the node exists and enable / disable the preference depending on the case
        if (KcalUtils.isKcalSupported()) {
            configurePreferences();
            KcalUtils.writeCurrentSettings(mSharedPrefs);
            configurePreferences();
        } else {
            mKcalSwitchPreference.setEnabled(false);
            mKcalSwitchPreference.setSummary(getString(R.string.kcal_not_supported));
            mResetButton.setEnabled(false);
            mColorProfilesButton.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(Constants.KEY_KCAL_ENABLE)) {
            KcalUtils.writeConfigToNode(Constants.KCAL_ENABLE_NODE, 0, (Boolean) newValue ? 1 : 0);
            configurePreferences();
            if (!mSharedPrefs.getBoolean(Constants.KEY_KCAL_ENABLE, false)) {
                FileUtils.writeLine(Constants.KCAL_SATURATION_NODE, 255);
                FileUtils.writeLine(Constants.KCAL_CONTRAST_NODE, 255);
                configurePreferences();
            }
        }
        return true;
    }

    // Configure the switches, preferences and sliders
    private void configurePreferences() {
        mKcalSwitchPreference.setEnabled(true);
        mKcalSwitchPreference.addOnSwitchChangeListener(this);
        mKcalSwitchPreference = (MainSwitchPreference) findPreference(Constants.KEY_KCAL_ENABLE);
        mResetButton = (Preference) findPreference(Constants.KEY_KCAL_RESET_DEFAULT);
        mColorProfilesButton = (Preference) findPreference(Constants.KEY_KCAL_COLOR_PROFILES);

        if (!mSharedPrefs.getBoolean(Constants.KEY_KCAL_ENABLE, false)) {
            mResetButton.setEnabled(false);
            mColorProfilesButton.setEnabled(false);
        }

        // Set the preference so it resets all the other preference's values, and applies the configuration on click
        mResetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.clear();
                editor.commit();
                getPreferenceScreen().removeAll();
                addPreferencesFromResource(R.xml.kcal_settings);
                configurePreferences();
                KcalUtils.writeCurrentSettings(mSharedPrefs);
                configurePreferences();
                return true;
            }
        });

        mColorProfilesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getColorProfilesDialog().create().show();
                return true;
            }
        });
    }

    private AlertDialog.Builder getColorProfilesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.color_profiles);

        builder.setItems(R.array.color_profiles_names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KcalUtils.setColorProfile(which, mSharedPrefs);
                getPreferenceScreen().removeAll();
                addPreferencesFromResource(R.xml.kcal_settings);
                configurePreferences();
            }
        });

        return builder;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mKcalSwitchPreference.setChecked(isChecked);
        if (isChecked) {
            KcalUtils.writeConfigToNode(Constants.KCAL_ENABLE_NODE, 0, 1);
        } else {
            KcalUtils.writeConfigToNode(Constants.KCAL_ENABLE_NODE, 0, 0);
        }

        mResetButton.setEnabled(isChecked);
        mColorProfilesButton.setEnabled(isChecked);
    }
}
