/*
 * Copyright (C) 2018,2020 The LineageOS Project
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

package org.lineageos.settings.device.dirac;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;

public class DiracSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnCheckedChangeListener {

    private static final String TAG = "DiracSettingsFragment";

    private TextView mTextView;

    private ListPreference mHeadsetType;
    private ListPreference mPreset;
    private ListPreference mScenes;
    private SwitchPreference mHifi;
    private MainSwitchPreference mSwitch;

    private DiracUtils mDiracUtils;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_DIRAC_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                mSwitch.setChecked(intent.getBooleanExtra(Constants.DIRAC_STATE, false));
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.dirac_settings);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            mDiracUtils = DiracUtils.getInstance(getActivity());
        } catch (Exception e) {
            Log.d(TAG, "Dirac is not present in system");
        }

        boolean enhancerEnabled = mDiracUtils != null ? mDiracUtils.isDiracEnabled() : false;

        mHeadsetType = (ListPreference) findPreference(Constants.KEY_DIRAC_HEADSET);
        mHeadsetType.setOnPreferenceChangeListener(this);

        mPreset = (ListPreference) findPreference(Constants.KEY_DIRAC_PRESET);
        mPreset.setOnPreferenceChangeListener(this);

        mScenes = (ListPreference) findPreference(Constants.KEY_DIRAC_SCENE);
        mScenes.setOnPreferenceChangeListener(this);

        mHifi = (SwitchPreference) findPreference(Constants.KEY_DIRAC_HIFI);
        mHifi.setOnPreferenceChangeListener(this);

        mSwitch = (MainSwitchPreference) findPreference(Constants.KEY_DIRAC_ENABLE);
        mSwitch.addOnSwitchChangeListener(this);

        boolean hifiEnable = mDiracUtils.getHifiMode();
        mHeadsetType.setEnabled(!hifiEnable && enhancerEnabled);
        mPreset.setEnabled(!hifiEnable && enhancerEnabled);
        mScenes.setEnabled(!hifiEnable && enhancerEnabled);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_DIRAC_SETTING_CHANGED);
        getContext().registerReceiver(stateReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dirac,
                container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwitch.setChecked(mDiracUtils.isDiracEnabled());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(stateReceiver);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mDiracUtils == null) return false;
        switch (preference.getKey()) {
            case Constants.KEY_DIRAC_HEADSET:
                mDiracUtils.setHeadsetType(Integer.parseInt(newValue.toString()));
                return true;
            case Constants.KEY_DIRAC_HIFI:
                mDiracUtils.setHifiMode((Boolean) newValue ? 1 : 0);
                if (mDiracUtils.isDiracEnabled()) {
                    mHeadsetType.setEnabled(!(Boolean) newValue);
                    mPreset.setEnabled(!(Boolean) newValue);
                    mScenes.setEnabled(!(Boolean) newValue);
                }
                return true;
            case Constants.KEY_DIRAC_PRESET:
                mDiracUtils.setLevel((String) newValue);
                return true;
            case Constants.KEY_DIRAC_SCENE:
                mDiracUtils.setScenario(Integer.parseInt(newValue.toString()));
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSwitch.setChecked(isChecked);

        if (mDiracUtils == null) return;

        mDiracUtils.setMusic(isChecked);

        if (!mDiracUtils.getHifiMode()) {
            mHeadsetType.setEnabled(isChecked);
            mPreset.setEnabled(isChecked);
            mScenes.setEnabled(isChecked);
        }

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_DIRAC_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.DIRAC_STATE, isChecked);
        getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }
}
