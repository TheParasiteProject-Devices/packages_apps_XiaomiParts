/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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

package org.lineageos.settings.doze;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.doze.Constants;

public class DozeSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener,
        OnCheckedChangeListener {

    private MainSwitchPreference mSwitchBar;

    private SwitchPreference mAlwaysOnDisplayPreference;
    private SwitchPreference mWakeOnGesturePreference;
    private SwitchPreference mPickUpPreference;
    private SwitchPreference mRaiseToWakePreference;
    private SwitchPreference mHandwavePreference;
    private SwitchPreference mPocketPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.doze_settings);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.KEY_DOZE_SETTINGS,
                Activity.MODE_PRIVATE);
        if (savedInstanceState == null && !prefs.getBoolean(Constants.KEY_FIRST_HELP_SHOWN, false)) {
            showHelp();
        }

        boolean dozeEnabled = DozeUtils.isDozeEnabled(getActivity());
        mSwitchBar = (MainSwitchPreference) findPreference(Constants.KEY_DOZE_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(dozeEnabled);

        mAlwaysOnDisplayPreference = (SwitchPreference) findPreference(Constants.KEY_ALWAYS_ON_DISPLAY);
        mAlwaysOnDisplayPreference.setEnabled(dozeEnabled);
        mAlwaysOnDisplayPreference.setChecked(DozeUtils.isAlwaysOnEnabled(getActivity()));
        mAlwaysOnDisplayPreference.setOnPreferenceChangeListener(this);

        mWakeOnGesturePreference = (SwitchPreference) findPreference(Constants.KEY_WAKE_ON_GESTURE);
        mWakeOnGesturePreference.setEnabled(dozeEnabled);
        mWakeOnGesturePreference.setOnPreferenceChangeListener(this);

        PreferenceCategory proximitySensorCategory =
                (PreferenceCategory) getPreferenceScreen().findPreference(Constants.CATEG_PROX_SENSOR);

        mPickUpPreference = (SwitchPreference) findPreference(Constants.KEY_GESTURE_PICK_UP);
        mPickUpPreference.setEnabled(dozeEnabled);
        mPickUpPreference.setOnPreferenceChangeListener(this);

        mRaiseToWakePreference = (SwitchPreference) findPreference(Constants.KEY_GESTURE_RAISE_TO_WAKE);
        mRaiseToWakePreference.setEnabled(dozeEnabled);
        mRaiseToWakePreference.setOnPreferenceChangeListener(this);
        DozeUtils.setPickUp(findPreference(Constants.KEY_GESTURE_PICK_UP), DozeUtils.isRaiseToWakeEnabled(getActivity()));

        mHandwavePreference = (SwitchPreference) findPreference(Constants.KEY_GESTURE_HAND_WAVE);
        mHandwavePreference.setEnabled(dozeEnabled);
        mHandwavePreference.setOnPreferenceChangeListener(this);

        mPocketPreference = (SwitchPreference) findPreference(Constants.KEY_GESTURE_POCKET);
        mPocketPreference.setEnabled(dozeEnabled);
        mPocketPreference.setOnPreferenceChangeListener(this);

        // Hide proximity sensor related features if the device doesn't support them
        if (!DozeUtils.getProxCheckBeforePulse(getActivity())) {
            getPreferenceScreen().removePreference(proximitySensorCategory);
        }

        // Hide AOD if not supported and set all its dependents otherwise
        if (!DozeUtils.alwaysOnDisplayAvailable(getActivity())) {
            getPreferenceScreen().removePreference(mAlwaysOnDisplayPreference);
        } else {
            proximitySensorCategory.setDependency(Constants.KEY_ALWAYS_ON_DISPLAY);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        DozeUtils.enableGesture(getActivity(), preference.getKey(), (Boolean) newValue);
        DozeUtils.checkDozeService(getActivity());

        if (Constants.KEY_ALWAYS_ON_DISPLAY.equals(preference.getKey())) {
            DozeUtils.enableAlwaysOn(getActivity(), (Boolean) newValue);
        } else if (Constants.KEY_GESTURE_RAISE_TO_WAKE.equals(preference.getKey())) {
            DozeUtils.setPickUp(findPreference(Constants.KEY_GESTURE_PICK_UP), (Boolean) newValue);
        } 

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DozeUtils.enableDoze(getActivity(), isChecked);
        DozeUtils.checkDozeService(getActivity());

        mSwitchBar.setChecked(isChecked);

        if (!isChecked) {
            DozeUtils.enableAlwaysOn(getActivity(), false);
            mAlwaysOnDisplayPreference.setChecked(false);
        }
        mAlwaysOnDisplayPreference.setEnabled(isChecked);

        mWakeOnGesturePreference.setEnabled(isChecked);
        mPickUpPreference.setEnabled(isChecked);
        mRaiseToWakePreference.setEnabled(isChecked);
        mHandwavePreference.setEnabled(isChecked);
        mPocketPreference.setEnabled(isChecked);
    }

    public static class HelpDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.doze_settings_help_title)
                    .setMessage(R.string.doze_settings_help_text)
                    .setNegativeButton(R.string.dialog_ok, (dialog, which) -> dialog.cancel())
                    .create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().getSharedPreferences("doze_settings", Activity.MODE_PRIVATE)
                    .edit()
                    .putBoolean("first_help_shown", true)
                    .commit();
        }
    }

    private void showHelp() {
        HelpDialogFragment fragment = new HelpDialogFragment();
        fragment.show(getFragmentManager(), "help_dialog");
    }
}
