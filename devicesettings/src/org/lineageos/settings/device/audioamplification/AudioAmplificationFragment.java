/*
 * Copyright (C) 2018 The Asus-SDM660 Project
 * Copyright (C) 2017-2021 The LineageOS Project
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
 * limitations under the License
 */

package org.lineageos.settings.device.audioamplification;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import org.lineageos.settings.device.Constants;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;
import org.lineageos.settings.device.widget.CustomSeekBarPreference;

public class AudioAmplificationFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private CustomSeekBarPreference mHeadphoneGain;
    private CustomSeekBarPreference mMicrophoneGain;
    private CustomSeekBarPreference mSpeakerGain;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.audio_amplification_settings, rootKey);

        mHeadphoneGain = (CustomSeekBarPreference) findPreference(Constants.KEY_HEADPHONE_GAIN);
        mHeadphoneGain.setOnPreferenceChangeListener(this);

        mMicrophoneGain = (CustomSeekBarPreference) findPreference(Constants.KEY_MICROPHONE_GAIN);
        mMicrophoneGain.setOnPreferenceChangeListener(this);

        mSpeakerGain = (CustomSeekBarPreference) findPreference(Constants.KEY_SPEAKER_GAIN);
        mSpeakerGain.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.audio_amplification, container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case Constants.KEY_HEADPHONE_GAIN:
                FileUtils.writeLine(Constants.HEADPHONE_GAIN_NODE, value + " " + value);
                break;
            case Constants.KEY_MICROPHONE_GAIN:
                FileUtils.writeLine(Constants.MICROPHONE_GAIN_NODE, (int) value);
                break;
            case Constants.KEY_SPEAKER_GAIN:
                FileUtils.writeLine(Constants.SPEAKER_GAIN_NODE, (int) value);
                break;
            default:
                break;
        }
        return true;
    }
}
