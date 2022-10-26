/*
 * Copyright (C) 2021 chaldeaprjkt
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

package org.lineageos.settings.device.flashlight;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;
import org.lineageos.settings.device.widget.SeekBarPreference;

public class FlashlightBrightnessFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.flashlight_settings);

        final SeekBarPreference mFlashlightBrigtness = (SeekBarPreference) findPreference(FlashlightUtils.PREF_BRIGHTNESS);
        if (FileUtils.fileExists(FlashlightUtils.PATH_BRIGHTNESS)) {
            mFlashlightBrigtness.setEnabled(true);
            mFlashlightBrigtness.setOnPreferenceChangeListener(this);
        } else {
            mFlashlightBrigtness.setSummary(R.string.flashlight_brightness_summary_incompatible);
            mFlashlightBrigtness.setEnabled(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.flashlight, container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (FlashlightUtils.PREF_BRIGHTNESS.equals(preference.getKey())) {
            FlashlightUtils.applyBrightness(getContext(), (int) newValue);
        }

        return true;
    }
}
